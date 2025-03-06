package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.user.LoginRequest;
import com.example.demo.user.RegisterRequest;
import com.example.demo.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RedirectView register(@ModelAttribute RegisterRequest request) {
        User user = authService.register(request);
        return new RedirectView("/login.html");
    }
}
