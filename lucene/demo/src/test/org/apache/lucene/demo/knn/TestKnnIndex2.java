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
import org.apache.lucene.util.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestKnnIndex2 {


  @Test
  public void testWrite() throws IOException, URISyntaxException {
    List<Document> docs = new ArrayList<>();
    String fieldName = "knnFloatField";

    Path indexPath = Paths.get("/home/teaho/desktop/prog/lucene/knn-index");
    IndexWriter writer = new IndexWriter(FSDirectory.open(indexPath), new IndexWriterConfig());

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

  @Test
  public void testSearch() throws IOException {
    Path testVectors = getDataPath("../test-files/knn-index").resolve("knn-token-vectors");
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



  protected Path getDataPath(String name) throws IOException {
    try {
      return Paths.get(
          IOUtils.requireResourceNonNull(this.getClass().getResource(name), name).toURI());
    } catch (URISyntaxException e) {
      throw new AssertionError(e);
    }
  }
}
