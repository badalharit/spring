package com.example.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

import com.example.core.security.AdminSessionInterceptor;

@Controller
public class AdminController {

    private static final String USERNAME = "root";
    private static final String PASSWORD = "root123";

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (Objects.equals(USERNAME, username) && Objects.equals(PASSWORD, password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute(AdminSessionInterceptor.SESSION_ADMIN, true);
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute("loginError", true);
        return "redirect:/login";
    }

    @GetMapping(value = "/dashboard", produces = MediaType.TEXT_HTML_VALUE)
    public String dashboardPage() {
        return "dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(AdminSessionInterceptor.SESSION_ADMIN);
            session.invalidate();
        }
        return "redirect:/login";
    }
}

