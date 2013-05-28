package de.tuberlin.dima.cuttlefish.preprocessing.vectorization;

import com.google.common.io.Closeables;
import de.tuberlin.dima.cuttlefish.preprocessing.indexing.Indexer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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
          IntWritable.class, VectorWritable.class);
      IntWritable itemIDWritable = new IntWritable();
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

          }
        }

        itemIDWritable.set(itemID);
        vectorWritable.set(new SequentialAccessSparseVector(documentVector));
        writer.append(itemIDWritable, vectorWritable);

        numDocsVectorized++;
        if (numDocsVectorized % 100 == 0) {
          log.info("Vectorized {} documents", numDocsVectorized);
        }
      }

      log.info("Vectorized {} documents", numDocsVectorized);

    } finally {
      Closeables.close(reader, true);
      Closeables.close(writer, true);
    }
  }

  private int maxTermFrequency(Terms termFreqVector) throws Exception {

    int maxTermFrequency = 1;

    TermsEnum te = termFreqVector.iterator(null);

    while ((te.next()) != null) {
      int termFrequency = (int) te.totalTermFreq();
      maxTermFrequency = Math.max(maxTermFrequency, termFrequency);
    }

    return maxTermFrequency;
  }


}
