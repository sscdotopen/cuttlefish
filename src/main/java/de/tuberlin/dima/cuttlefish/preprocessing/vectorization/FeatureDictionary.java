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
