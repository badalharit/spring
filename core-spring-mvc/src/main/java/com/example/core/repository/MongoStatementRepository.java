package com.example.core.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class MongoStatementRepository {

    private static final String MONGO_URI = "mongodb://localhost:27017/";
    private static final String DB_NAME = "spring_statements";
    private static final String COLLECTION = "statements";

    public String insertPayload(Map<String, Object> payload) {
        Map<String, Object> docMap = payload == null ? new HashMap<>() : payload;

        Document doc = new Document(docMap);
        doc.append("createdAt", Instant.now().toString());

        try (MongoClient client = MongoClients.create(MONGO_URI)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> col = db.getCollection(COLLECTION);
            org.bson.types.ObjectId id = col.insertOne(doc).getInsertedId().asObjectId().getValue();
            return id.toHexString();
        }
    }

    /**
     * @return JSON-like map with:
     *  - items: list of documents
     *  - page, size, total, totalPages
     */
    public Map<String, Object> listStatements(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(100, size));

        try (MongoClient client = MongoClients.create(MONGO_URI)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> col = db.getCollection(COLLECTION);

            long total = col.countDocuments();
            long totalPagesLong = (total + safeSize - 1) / safeSize;
            int totalPages = (int) totalPagesLong;

            java.util.List<Document> items = new java.util.ArrayList<>();

            // newest first
            col.find()
                    .sort(new Document("createdAt", -1))
                    .skip(safePage * safeSize)
                    .limit(safeSize)
                    .into(items);


            // Convert _id to string for easier UI
            List<Map<String, Object>> converted = new java.util.ArrayList<>();
            for (Document d : items) {
                Map<String, Object> m = new java.util.LinkedHashMap<>();
                for (Map.Entry<String, Object> e : d.entrySet()) {
                    if ("_id".equals(e.getKey())) {
                        Object v = e.getValue();
                        m.put("_id", v == null ? null : v.toString());
                    } else {
                        m.put(e.getKey(), e.getValue());
                    }
                }
                converted.add(m);
            }

            Map<String, Object> out = new java.util.LinkedHashMap<>();
            out.put("items", converted);
            out.put("page", safePage);
            out.put("size", safeSize);
            out.put("total", total);
            out.put("totalPages", totalPages);
            return out;
        }
    }
}


