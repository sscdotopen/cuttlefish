package de.tuberlin.dima.cuttlefish.preprocessing.indexing;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

public class StringFieldWithTermVectors extends Field {

  public static final FieldType TYPE = new FieldType();

  static {
    TYPE.setIndexed(true);
    TYPE.setOmitNorms(true);
    TYPE.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS);
    TYPE.setStored(true);
    TYPE.setTokenized(false);
    TYPE.setStoreTermVectors(true);
    TYPE.freeze();
  }

  public StringFieldWithTermVectors(String name, String value) {
    super(name, value, TYPE);
  }

}
