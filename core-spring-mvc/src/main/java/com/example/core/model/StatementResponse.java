package com.example.core.model;

public class StatementResponse {
    private String status;
    private String mongoId;
    private boolean rabbitPublished;

    public StatementResponse() {
    }

    public StatementResponse(String status, String mongoId, boolean rabbitPublished) {
        this.status = status;
        this.mongoId = mongoId;
        this.rabbitPublished = rabbitPublished;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public boolean isRabbitPublished() {
        return rabbitPublished;
    }

    public void setRabbitPublished(boolean rabbitPublished) {
        this.rabbitPublished = rabbitPublished;
    }
}

