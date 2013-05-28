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

package de.tuberlin.dima.cuttlefish;

import de.tuberlin.dima.cuttlefish.preprocessing.DefaultFeatureExtraction;
import de.tuberlin.dima.cuttlefish.preprocessing.DefaultFeatureWeighting;
import de.tuberlin.dima.cuttlefish.preprocessing.indexing.FeatureExtraction;
import de.tuberlin.dima.cuttlefish.preprocessing.indexing.Indexer;
import de.tuberlin.dima.cuttlefish.preprocessing.parsing.NewsItemExtractor;
import de.tuberlin.dima.cuttlefish.preprocessing.vectorization.FeatureWeighting;
import de.tuberlin.dima.cuttlefish.preprocessing.vectorization.Vectorizer;

import java.io.File;

public class PreprocessingPipeline {

  public static void main(String[] args) throws Exception {

    //-----------------------------------------------------------------------------

    File[] zippedArchivesDirs = { new File("/home/ssc/Desktop/cuttlefish/archives/") };
    File outputDir = new File("/home/ssc/Desktop/cuttlefish/output");

    FeatureExtraction featureExtraction = new DefaultFeatureExtraction();
    FeatureWeighting featureWeighting = new DefaultFeatureWeighting();

    //-----------------------------------------------------------------------------
    File indexDir = new File(outputDir, "/index/");
    indexDir.mkdirs();
    File vectorsDir = new File(outputDir, "/vectors/");
    vectorsDir.mkdirs();

    Indexer indexer = new Indexer(new NewsItemExtractor(zippedArchivesDirs), featureExtraction);

    indexer.index(indexDir);

    Vectorizer vectorizer = new Vectorizer(featureWeighting);

    vectorizer.vectorize(indexDir, vectorsDir);


  }



}
