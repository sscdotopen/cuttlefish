package de.tuberlin.dima.cuttlefish.preprocessing.vectorization;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public class IDAndCodes implements WritableComparable<IDAndCodes> {

  private int id;
  private Multimap<String, String> codes;

  public void set(int id, Multimap<String, String> codes) {
    this.id = id;
    this.codes = codes;
  }

  public int id() {
    return id;
  }

  public Multimap<String, String> codes() {
    return codes;
  }

  @Override
  public int compareTo(IDAndCodes o) {
    return Ints.compare(id, o.id);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(id);
    out.writeInt(codes.size());

    for (Map.Entry<String,String> entry : codes.entries()) {
      out.writeUTF(entry.getKey());
      out.writeUTF(entry.getValue());
    }
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    id = in.readInt();

    codes = ArrayListMultimap.create();
    int numCodes = in.readInt();
    for (int index = 0; index < numCodes; index++) {
      codes.put(in.readUTF(), in.readUTF());
    }
  }
}
