package de.tuberlin.dima.cuttlefish.preprocessing.vectorization;

import org.apache.mahout.math.Vector;

public interface FeatureWeighting {

  double weight(String fieldName, String term, int termFrequency, int documentFrequency, int maxTermFrequency,
                int numDocs);

  void normalize(Vector documentVector);

}
