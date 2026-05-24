package com.example.core.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthFilter implements Filter {

    private final String expectedUsername;
    private final String expectedPassword;

    public BasicAuthFilter() {
        this("root", "root123");
    }

    public BasicAuthFilter(String expectedUsername, String expectedPassword) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            reject(res);
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decoded, StandardCharsets.UTF_8);

        int idx = credentials.indexOf(':');
        if (idx < 0) {
            reject(res);
            return;
        }

        String username = credentials.substring(0, idx);
        String password = credentials.substring(idx + 1);

        if (!expectedUsername.equals(username) || !expectedPassword.equals(password)) {
            reject(res);
            return;
        }

        chain.doFilter(request, response);
    }

    private void reject(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setHeader("WWW-Authenticate", "Basic realm=\"statement-api\"");
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"unauthorized\"}");
    }
}

