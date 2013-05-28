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

    //-----------------------------------------------------------------------------

    String documentVectorsFile = "/home/ssc/Desktop/cuttlefish/output/vectors/documentVectors.seq";

    //-----------------------------------------------------------------------------

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
