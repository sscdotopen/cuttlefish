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
