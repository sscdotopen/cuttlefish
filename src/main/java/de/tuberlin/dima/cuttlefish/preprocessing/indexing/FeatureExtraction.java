package de.tuberlin.dima.cuttlefish.preprocessing.indexing;

import de.tuberlin.dima.cuttlefish.preprocessing.NewsItem;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;

public interface FeatureExtraction {

  Analyzer analyzerToUse();

  Document asDocument(NewsItem newsItem);

}
