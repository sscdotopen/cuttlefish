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

package de.tuberlin.dima.cuttlefish.preprocessing.indexing;

import de.tuberlin.dima.cuttlefish.preprocessing.NewsItem;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;

public interface FeatureExtraction {

  Analyzer analyzerToUse();

  Document asDocument(NewsItem newsItem);

}
