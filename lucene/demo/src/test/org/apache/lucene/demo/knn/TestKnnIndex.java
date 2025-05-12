/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.demo.knn;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnFloatVectorQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestKnnIndex extends LuceneTestCase {

  public void testBuild() throws IOException {
    Path testVectors = getDataPath("../test-files/knn-dict").resolve("knn-token-vectors");

    try (Directory directory = newDirectory()) {
      KnnVectorDict.build(testVectors, directory, "dict");
      List<Document> docs = new ArrayList<>();
      String fieldName = "knnFloatField";
//      IndexWriter writer = new IndexWriter(FSDirectory.open(/* ... */), new IndexWriterConfig());
//
//      for (float[] vector : /* ... */) {
//        Document doc = new Document();
//        doc.add(new KnnFloatVectorField(fieldName, vector, VectorSimilarityFunction.EUCLIDEAN));
//        docs.add(doc);
//        // ... 其他字段的添加 ...
//        writer.addDocument(doc);
//      }

//      try (KnnVectorDict dict = new KnnVectorDict(directory, "dict")) {
//        assertEquals(50, dict.getDimension());
//        byte[] vector = new byte[dict.getDimension() * Float.BYTES];
//
//        // not found token has zero vector
//        dict.get(new BytesRef("never saw this token"), vector);
//        assertArrayEquals(new byte[200], vector);
//
//        // found token has nonzero vector
//        dict.get(new BytesRef("the"), vector);
//        assertFalse(Arrays.equals(new byte[200], vector));
//
//        // incorrect dimension for output buffer
//        expectThrows(
//            IllegalArgumentException.class, () -> dict.get(new BytesRef("the"), new byte[10]));
//      }
    }
  }



  public void testWrite() throws IOException {
    List<Document> docs = new ArrayList<>();
    String fieldName = "knnFloatField";

    Path testVectors = getDataPath("../test-files/my-knn").resolve("knn-token-vectors");
    IndexWriter writer = new IndexWriter(FSDirectory.open(testVectors), new IndexWriterConfig());

    List<float[]> list = new ArrayList<>();
    list.add(new float[]{0.1111f, 1.2345f});
    for (float[] vector : list) {
      Document doc = new Document();
      doc.add(new KnnFloatVectorField(fieldName, vector, VectorSimilarityFunction.EUCLIDEAN));
      docs.add(doc);
      // ... 其他字段的添加 ...
      writer.addDocument(doc);
    }

    writer.close();
  }


  public void testSearch() throws IOException {
    Path testVectors = getDataPath("../test-files/my-knn").resolve("knn-token-vectors");
    try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(testVectors))) {
      IndexSearcher searcher = new IndexSearcher(reader);

      float[] targetVector = { /* ... */ }; // 目标向量
      int k = 3; // 想要检索的最近邻个数
      KnnFloatVectorQuery knnQuery = new KnnFloatVectorQuery("knnFloatField", targetVector, k);

      TopDocs topDocs = searcher.search(knnQuery, 10);
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        // 处理检索到的文档
      }
    }
  }
}
