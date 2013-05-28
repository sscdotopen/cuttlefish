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

package de.tuberlin.dima.cuttlefish.preprocessing.vectorization;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Closeables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

public class Vectorizer {

  private final FeatureWeighting weighting;

  private static final Logger log = LoggerFactory.getLogger(Vectorizer.class);

  public Vectorizer(FeatureWeighting weighting) {
    this.weighting = weighting;
  }

  public void vectorize(File luceneIndexDir, File outputDir) throws Exception {

    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.getLocal(conf);
    SequenceFile.Writer writer = null;

    FeatureDictionary dict = new FeatureDictionary();

    DirectoryReader reader = null;
    try {
      reader = DirectoryReader.open(new SimpleFSDirectory(luceneIndexDir));

      writer = SequenceFile.createWriter(fs, conf, new Path(outputDir.toString(), "documentVectors.seq"),
          IDAndCodes.class, VectorWritable.class);
      IDAndCodes idAndCodes = new IDAndCodes();
      VectorWritable vectorWritable = new VectorWritable();

      Fields fields = MultiFields.getFields(reader);
      if (fields != null) {
        Iterator<String> fieldNames = fields.iterator();
        while (fieldNames.hasNext()) {
          String field = fieldNames.next();
          if (!field.startsWith("bip:") && !"itemID".equals(field)) {

            Terms terms = fields.terms(field);
            TermsEnum termsEnum = terms.iterator(null);
            BytesRef text;
            while ((text = termsEnum.next()) != null) {
              dict.addTextFeature(field, text.utf8ToString());
            }
          }
        }
      }

      int numDocsVectorized = 0;

      for (int docID = 0; docID < reader.maxDoc(); docID++) {
        Document doc = reader.document(docID);

        int itemID = doc.getField("itemID").numericValue().intValue();

        RandomAccessSparseVector documentVector = new RandomAccessSparseVector(dict.numFeatures());
        Multimap<String, String> codes = HashMultimap.create();

        for (IndexableField field : doc.getFields()) {

          String fieldName = field.name();

          if (!fieldName.startsWith("bip:") && !"itemID".equals(fieldName)) {

            Terms termFreqVector = reader.getTermVector(docID, fieldName);

            if (termFreqVector != null) {

              int maxTermFrequency = maxTermFrequency(termFreqVector);

              TermsEnum te = termFreqVector.iterator(null);
              BytesRef term;

              while ((term = te.next()) != null) {

                String termStr = term.utf8ToString();
                int termFrequency = (int) te.totalTermFreq();

                int documentFrequency = reader.docFreq(new Term(fieldName, term));
                int numDocs = reader.numDocs();

                double weight = weighting.weight(fieldName, termStr, termFrequency, documentFrequency, maxTermFrequency,
                                                 numDocs);

                int featureIndex = dict.index(fieldName, term.utf8ToString());
                documentVector.setQuick(featureIndex, weight);
              }
            }

          } else if (fieldName.startsWith("bip:")) {
            for (String value : doc.getValues(fieldName)) {
              codes.put(fieldName, value);
            }
          }
        }

        Vector featureVector = new SequentialAccessSparseVector(documentVector);

        weighting.normalize(featureVector);

        idAndCodes.set(itemID, codes);
        vectorWritable.set(featureVector);
        writer.append(idAndCodes, vectorWritable);

        numDocsVectorized++;
        if (numDocsVectorized % 100 == 0) {
          log.info("Vectorized {} documents", numDocsVectorized);
        }
      }

      log.info("Vectorized {} documents", numDocsVectorized);

      dict.writeToFile(new File(outputDir, "features.txt"));

      log.info("Wrote feature dictionary");

    } finally {
      Closeables.close(reader, true);
      Closeables.close(writer, true);
    }

  }

  private int maxTermFrequency(Terms termFreqVector) throws Exception {

    int maxTermFrequency = 1;

    TermsEnum te = termFreqVector.iterator(null);

    while (te.next() != null) {
      int termFrequency = (int) te.totalTermFreq();
      maxTermFrequency = Math.max(maxTermFrequency, termFrequency);
    }

    return maxTermFrequency;
  }


}
