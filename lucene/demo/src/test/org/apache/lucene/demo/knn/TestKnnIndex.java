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
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KeywordField;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnFloatVectorQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


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
    Path testVectors = getDataPath("../test-files/knn-dict").resolve("knn-token-vectors");

    List<String> lines = Files.readAllLines(testVectors);
    Map<String, float[]> map = new HashMap<>();
    for (String line : lines) {
      String[] strs = line.split(" ");
      String key = strs[0];
      float[] floatSubArray = new float[strs.length-1];
      for (int i = 1; i < strs.length; i++) {
        floatSubArray[i-1] = Float.parseFloat(strs[i]);
      }
      map.put(key, floatSubArray);
    }

    List<Document> docs = new ArrayList<>();
    String fieldName = "knnFloatField";

    Path indexPath = Paths.get("/home/teaho/desktop/prog/lucene/knn-index");
    IndexWriter writer = new IndexWriter(FSDirectory.open(indexPath), new IndexWriterConfig());

    for (Map.Entry<String, float[]> stringEntry : map.entrySet()) {
      Document doc = new Document();
      doc.add(new KeywordField("name", stringEntry.getKey(), Field.Store.YES));
      doc.add(new KnnFloatVectorField(fieldName, stringEntry.getValue(), VectorSimilarityFunction.EUCLIDEAN));
      docs.add(doc);
      // ... 其他字段的添加 ...
      writer.addDocument(doc);
    }

    writer.close();
  }


  public void testSearch() throws IOException {
    Path testVectors = Paths.get("/home/teaho/desktop/prog/lucene/knn-index");
    try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(testVectors))) {
      IndexSearcher searcher = new IndexSearcher(reader);

      float[] targetVector = new float[]{0.81876f, -0.28243f, -0.41366f, -0.35102f, 0.085195f, -0.90606f, -0.22238f, 0.94749f, 0.049761f, -0.6503f, -0.41098f, -0.0070715f, 0.44125f, 0.51997f, 0.49135f, 0.41949f, 0.46956f, -0.25245f, 0.13239f, -0.5308f, 0.76648f, -0.076025f, 0.43355f, -0.018653f, -0.0030364f, -0.80093f, 0.040844f, -0.75689f, 0.35041f, -0.23985f, 2.5909f, 1.0013f, -1.7745f, -0.40713f, 0.23207f, 0.78183f, 0.088342f, 0.54988f, 0.10473f, -0.46467f, -0.47361f, -0.47255f, 0.33408f, -0.29324f, 0.74618f, 0.78208f, 0.37266f, 0.60175f, -0.23775f, 0.3695f}; // 目标向量
      int k = 3; // 想要检索的最近邻个数
      KnnFloatVectorQuery knnQuery = new KnnFloatVectorQuery("knnFloatField", targetVector, k);

      TopDocs topDocs = searcher.search(knnQuery, 10);
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        // 处理检索到的文档
        Document doc = searcher.getIndexReader().storedFields().document(scoreDoc.doc);

        // 获取文档所有字段
        List<IndexableField> fields = doc.getFields();
        for (IndexableField field : fields) {
          String fieldName = field.name();
          String fieldValue = doc.get(fieldName);
          System.out.println(fieldName + ": " + fieldValue);
        }
      }
    }
  }
}
