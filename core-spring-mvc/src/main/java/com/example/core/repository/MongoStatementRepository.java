package com.example.core.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
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
}

