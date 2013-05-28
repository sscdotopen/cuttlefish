package de.tuberlin.dima.cuttlefish.preprocessing.vectorization;

import com.google.common.collect.Maps;

import java.util.Map;

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


}
