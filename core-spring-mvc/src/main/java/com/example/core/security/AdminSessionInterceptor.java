package com.example.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminSessionInterceptor implements HandlerInterceptor {

    public static final String SESSION_ADMIN = "admin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Protect dashboard endpoints
        if (path.endsWith("/dashboard") || path.endsWith("/api/statements")) {
            HttpSession session = request.getSession(false);
            boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute(SESSION_ADMIN));
            if (!isAdmin) {
                // simple redirect to login
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}

