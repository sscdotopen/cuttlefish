package de.tuberlin.dima.cuttlefish.indexing;

import de.tuberlin.dima.cuttlefish.parsing.NewsItem;
import de.tuberlin.dima.cuttlefish.parsing.NewsItemExtractor;
import de.tuberlin.dima.cuttlefish.parsing.NewsItemProcessor;
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
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {

  public static final String ITEM_ID = "itemID";
  public static final String YEAR = "year";
  public static final String MONTH = "month";
  public static final String DAY = "day";
  public static final String TITLE = "title";
  public static final String HEADLINE = "headline";
  public static final String DATELINE = "dateline";
  public static final String TEXT = "text";
  public static final String CODE = "code";

  private static final Logger log = LoggerFactory.getLogger(Indexer.class);

  public Indexer() {}

  public void index(NewsItemExtractor extractor, File indexDir) throws Exception {
    Directory index = new SimpleFSDirectory(indexDir);
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);
    final IndexWriter writer = new IndexWriter(index, config);

    final AtomicInteger numDocsIndexed = new AtomicInteger(0);

    try {

      extractor.extract(new NewsItemProcessor() {
        @Override
        public void process(NewsItem newsItem) {

          try {
            writer.addDocument(asDocument(newsItem));
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

  private Document asDocument(NewsItem newsItem) {
    Document doc = new Document();

    doc.add(new IntField(ITEM_ID, newsItem.itemID(), Field.Store.YES));
    doc.add(new StringField(YEAR, String.valueOf(newsItem.date().getYear()), Field.Store.YES));
    doc.add(new StringField(MONTH, String.valueOf(newsItem.date().getMonth()), Field.Store.YES));
    doc.add(new StringField(DAY, String.valueOf(newsItem.date().getDay()), Field.Store.YES));
    doc.add(new TextField(TITLE, newsItem.title(), Field.Store.YES));
    doc.add(new TextField(HEADLINE, newsItem.headline(), Field.Store.YES));
    doc.add(new TextField(DATELINE, newsItem.dateline(), Field.Store.YES));
    doc.add(new TextField(TEXT, newsItem.text(), Field.Store.YES));

    for (String code : newsItem.codes()) {
      doc.add(new TextField(CODE, code, Field.Store.YES));
    }

    for (Map.Entry<String,String> dcEntry : newsItem.dcs().entrySet()) {
      doc.add(new StringField(dcEntry.getKey(), dcEntry.getValue(), Field.Store.YES));
    }
    return doc;
  }

}
