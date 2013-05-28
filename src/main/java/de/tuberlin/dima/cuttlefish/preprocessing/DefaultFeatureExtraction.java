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

import de.tuberlin.dima.cuttlefish.preprocessing.indexing.FeatureExtraction;
import de.tuberlin.dima.cuttlefish.preprocessing.indexing.StringFieldWithTermVectors;
import de.tuberlin.dima.cuttlefish.preprocessing.indexing.TextFieldWithTermVectors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.util.Version;

import java.util.Map;

public class DefaultFeatureExtraction implements FeatureExtraction {

  public static final String ITEM_ID = "itemID";
  public static final String YEAR = "year";
  public static final String MONTH = "month";
  public static final String DAY = "day";
  public static final String TITLE = "title";
  public static final String HEADLINE = "headline";
  public static final String DATELINE = "dateline";
  public static final String TEXT = "text";

  @Override
  public Analyzer analyzerToUse() {
    return new EnglishAnalyzer(Version.LUCENE_42);
  }

  @Override
  public Document asDocument(NewsItem newsItem) {
    Document doc = new Document();

    doc.add(new IntField(ITEM_ID, newsItem.itemID(), Field.Store.YES));
    doc.add(new StringFieldWithTermVectors(YEAR, String.valueOf(newsItem.date().getYear())));
    doc.add(new StringFieldWithTermVectors(MONTH, String.valueOf(newsItem.date().getMonth())));
    doc.add(new StringFieldWithTermVectors(DAY, String.valueOf(newsItem.date().getDay())));
    doc.add(new TextFieldWithTermVectors(TITLE, newsItem.title()));
    doc.add(new TextFieldWithTermVectors(HEADLINE, newsItem.headline()));
    doc.add(new TextFieldWithTermVectors(DATELINE, newsItem.dateline()));
    doc.add(new TextFieldWithTermVectors(TEXT, newsItem.text()));

    for (Map.Entry<String,String> entry : newsItem.codes().entries()) {
      doc.add(new StringFieldWithTermVectors(entry.getKey(), entry.getValue()));
    }
    for (Map.Entry<String,String> entry : newsItem.dcs().entrySet()) {
      doc.add(new StringFieldWithTermVectors(entry.getKey(), entry.getValue()));
    }
    return doc;
  }
}
