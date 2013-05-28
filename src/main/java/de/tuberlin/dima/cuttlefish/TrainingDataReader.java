package de.tuberlin.dima.cuttlefish;

import com.google.common.collect.Multimap;
import de.tuberlin.dima.cuttlefish.preprocessing.vectorization.IDAndCodes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

import java.util.Map;

public class TrainingDataReader {

  public static void main(String[] args) {

    String documentVectorsFile = "/home/ssc/Desktop/cuttlefish/output/vectors/documentVectors.seq";

    //----------------------------------------------------------------------

    Configuration conf = new Configuration();

    int n = 0;

    for (Pair<IDAndCodes, VectorWritable> labeledArticle : 
        new SequenceFileIterable<IDAndCodes, VectorWritable>(new Path(documentVectorsFile), conf)) {

      System.out.println("ID: " + labeledArticle.getFirst().id());

      Vector features = labeledArticle.getSecond().get();

      System.out.println("Features: " + features.getNumNondefaultElements() + " of " + features.size());

      Multimap<String,String> codes = labeledArticle.getFirst().codes();
      for (Map.Entry<String, String> codeEntry : codes.entries()) {
        System.out.println("\t" + codeEntry.getKey() + "=" + codeEntry.getValue());
      }


      if (n++ == 10) {
        break;
      }
    }

  }

}
