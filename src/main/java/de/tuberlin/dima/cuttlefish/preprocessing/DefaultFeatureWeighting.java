package de.tuberlin.dima.cuttlefish.preprocessing;

import com.google.common.collect.Sets;
import de.tuberlin.dima.cuttlefish.preprocessing.vectorization.FeatureWeighting;
import org.apache.mahout.math.Vector;

import java.util.Set;

public class DefaultFeatureWeighting implements FeatureWeighting {

  private static final Set<String> TEXT_FIELDS =
      Sets.newHashSet(DefaultFeatureExtraction.TITLE, DefaultFeatureExtraction.HEADLINE,
                      DefaultFeatureExtraction.DATELINE, DefaultFeatureExtraction.TEXT);

  @Override
  public double weight(String fieldName, String term, int termFrequency, int documentFrequency, int maxTermFrequency,
                       int numDocs) {

    boolean useTfIdf = TEXT_FIELDS.contains(fieldName);

    double weight = 1;

    if (useTfIdf) {
      // http://en.wikipedia.org/wiki/Tf%E2%80%93idf
      weight = (0.5 + ((0.5 * (double) termFrequency) / (double) maxTermFrequency))
               * Math.log((double) numDocs / documentFrequency);
    }

    return weight;
  }

  @Override
  public void normalize(Vector documentVector) {
    //TODO scale to unit length?
  }
}
