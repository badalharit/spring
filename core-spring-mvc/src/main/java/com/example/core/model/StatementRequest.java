package com.example.core.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Map;

public class StatementRequest {

    private Map<String, Object> payload;

    @JsonAnySetter
    public void setAny(String key, Object value) {
        if (payload == null) {
            payload = new java.util.LinkedHashMap<>();
        }
        payload.put(key, value);
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}

