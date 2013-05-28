package de.tuberlin.dima.cuttlefish.preprocessing.indexing;

import de.tuberlin.dima.cuttlefish.preprocessing.NewsItem;
import de.tuberlin.dima.cuttlefish.preprocessing.parsing.NewsItemExtractor;
import de.tuberlin.dima.cuttlefish.preprocessing.parsing.NewsItemProcessor;
import org.apache.lucene.analysis.Analyzer;
/*
 * Copyright (C) 2013 Database Systems and Information Management Group,
 * TU Berlin
 *
 * relation-discovery is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * relation-discovery is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with relation-discovery; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {

  private final NewsItemExtractor newsItemExtractor;
  private final FeatureExtraction featureExtraction;

  private static final Logger log = LoggerFactory.getLogger(Indexer.class);

  public Indexer(NewsItemExtractor newsItemExtractor, FeatureExtraction featureExtraction) {
    this.newsItemExtractor = newsItemExtractor;
    this.featureExtraction = featureExtraction;
  }

  public void index(File indexDir) throws Exception {
    Directory index = new SimpleFSDirectory(indexDir);
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);
    final IndexWriter writer = new IndexWriter(index, config);

    final AtomicInteger numDocsIndexed = new AtomicInteger(0);

    try {

      newsItemExtractor.extract(new NewsItemProcessor() {
        @Override
        public void process(NewsItem newsItem) {

          try {
            writer.addDocument(featureExtraction.asDocument(newsItem));
            int numDocs = numDocsIndexed.incrementAndGet();
            if (numDocs % 100 == 0) {
              log.info("Indexed {} news articles", numDocs);
            }

          } catch (IOException e) {
            log.error("Failed to index news item", e);
          }
        }
      });

    } finally {
      writer.close(true);
    }

    log.info("Indexed {} news articles", numDocsIndexed.get());
  }

}
