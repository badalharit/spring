package com.example.core.service;

import com.example.core.model.StatementRequest;
import com.example.core.model.StatementResponse;
import com.example.core.rabbit.RabbitStatementPublisher;
import com.example.core.repository.MongoStatementRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class StatementService {

    private final MongoStatementRepository mongo;
    private final RabbitStatementPublisher rabbit;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatementService(MongoStatementRepository mongo, RabbitStatementPublisher rabbit) {
        this.mongo = mongo;
        this.rabbit = rabbit;
    }

    public StatementResponse createStatement(StatementRequest req) {
        String mongoId = mongo.insertPayload(req.getPayload());

        String json;
        try {
            json = objectMapper.writeValueAsString(req.getPayload());
        } catch (JsonProcessingException e) {
            json = "{}";
        }

        boolean published = rabbit.publish(json);
        return new StatementResponse("ok", mongoId, published);
    }
}

