package com.example.core.controller;

import com.example.core.repository.MongoStatementRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class StatementsDashboardApiController {

    private final MongoStatementRepository repo;

    public StatementsDashboardApiController(MongoStatementRepository repo) {
        this.repo = repo;
    }

    @GetMapping(value = "/api/statements", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return repo.listStatements(page, size);
    }
}

