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

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class FeatureDictionary {

  private final Map<String, Map<String,Integer>> textFeatureOffsets = Maps.newHashMap();

  private int index = 0;

  public void addTextFeature(String field, String term) {
    if (!textFeatureOffsets.containsKey(field)) {
      textFeatureOffsets.put(field, Maps.<String,Integer>newHashMap());
    }
    textFeatureOffsets.get(field).put(term, index);
    index++;
  }

  public int index(String field, String term) {
    return textFeatureOffsets.get(field).get(term);
  }

  public int numFeatures() {
    return index;
  }

  public void writeToFile(File file) throws IOException {
    SortedMap<Integer, String> features = new TreeMap<Integer, String>();

    for (Map.Entry<String, Map<String, Integer>> fieldEntry : textFeatureOffsets.entrySet()) {
      String field = fieldEntry.getKey();
      for (Map.Entry<String, Integer> featureWithOffset : fieldEntry.getValue().entrySet()) {
        features.put(featureWithOffset.getValue(), field + "=" + featureWithOffset.getKey());
      }
    }

    BufferedWriter writer = null;

    try {
      writer = new BufferedWriter(new FileWriter(file));

      for (Map.Entry<Integer, String> feature : features.entrySet()) {
        writer.write(String.valueOf(feature.getKey()));
        writer.write(" ");
        writer.write(feature.getValue());
        writer.newLine();
      }

    } finally {
      Closeables.close(writer, true);
    }
  }


}
