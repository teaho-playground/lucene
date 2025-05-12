//package org.apache.lucene.demo.knn;
//
//
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.KnnFloatVectorField;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.index.VectorSimilarityFunction;
//import org.apache.lucene.store.FSDirectory;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * @author hetingleong@xiaomi.com
// * @date 2025-05
// */
//public class VectorIndexing {
//    public static void main(String[] args) throws IOException {
//        List<Document> docs = new ArrayList<>();
//        String fieldName = "knnFloatField";
//        IndexWriter writer = new IndexWriter(FSDirectory.open(/* ... */), new IndexWriterConfig());
//
//        for (float[] vector : /* ... */) {
//            Document doc = new Document();
//            doc.add(new KnnFloatVectorField(fieldName, vector, VectorSimilarityFunction.EUCLIDEAN));
//            docs.add(doc);
//            // ... 其他字段的添加 ...
//            writer.addDocument(doc);
//        }
//
//        writer.close();
//    }
//}