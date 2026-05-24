package com.example.core.controller;

import com.example.core.model.StatementRequest;
import com.example.core.model.StatementResponse;
import com.example.core.service.StatementService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatementController {

    private final StatementService service;

    public StatementController(StatementService service) {
        this.service = service;
    }

    @PostMapping(path = "/statement", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public StatementResponse statement(@RequestBody StatementRequest request) {
        return service.createStatement(request);
    }
}

