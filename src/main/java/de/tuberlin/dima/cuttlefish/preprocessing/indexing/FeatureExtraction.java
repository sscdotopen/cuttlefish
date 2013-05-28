package de.tuberlin.dima.cuttlefish.preprocessing.indexing;

import de.tuberlin.dima.cuttlefish.preprocessing.NewsItem;
import org.apache.lucene.document.Document;

public interface FeatureExtraction {

  Document asDocument(NewsItem newsItem);

}
