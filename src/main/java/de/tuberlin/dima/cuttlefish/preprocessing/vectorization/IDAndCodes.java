/*
 * Copyright (C) 2013 Database Systems and Information Management Group,
 * TU Berlin
 *
 * cuttlefish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * cuttlefish is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cuttlefish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package de.tuberlin.dima.cuttlefish.preprocessing.vectorization;

import com.google.common.collect.HashMultimap;
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

    codes = HashMultimap.create();
    int numCodes = in.readInt();
    for (int index = 0; index < numCodes; index++) {
      codes.put(in.readUTF(), in.readUTF());
    }
  }
}
