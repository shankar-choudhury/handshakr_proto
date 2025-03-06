package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.auth.JwtService;
import com.example.demo.user.LoginRequest;
import com.example.demo.user.RegisterRequest;
import com.example.demo.user.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthService authService, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public RedirectView register(@ModelAttribute RegisterRequest request) {
        User user = authService.register(request);
        return new RedirectView("/login.html");
    }

    @PostMapping("/login")
    public RedirectView login(@ModelAttribute LoginRequest request, HttpServletResponse response) {
        authService.authenticate(request);

        UserDetails details = userDetailsService.loadUserByUsername(request.username());

        String jwtToken = jwtService.generateToken(details);

        response.setHeader("Authorization", "Bearer " + jwtToken);

        return new RedirectView("./home.html");
    }
}
