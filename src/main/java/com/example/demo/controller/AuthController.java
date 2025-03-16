package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.auth.Constants;
import com.example.demo.auth.JwtService;
import com.example.demo.user.LoginRequest;
import com.example.demo.user.RegisterRequest;
import com.example.demo.user.User;
import jakarta.servlet.http.Cookie;
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

        response.addCookie(createJwtCookie(jwtToken, Constants.COOKIE_EXPIRATION));

        return new RedirectView("/home.html");
    }

    @PostMapping("/logout")
    public RedirectView logout(HttpServletResponse response) {
        response.addCookie(createJwtCookie(null, 0)); // Invalidate cookie given by login by having browser overwrite cookie with same name

        return new RedirectView("/login.html");
    }

    private Cookie createJwtCookie(String jwtToken, int maxAge) {
        Cookie jwtCookie = new Cookie(Constants.COOKIE_NAME, jwtToken);
        jwtCookie.setHttpOnly(true); //Useful to prevent CSRF attacks
        jwtCookie.setSecure(false); //Set to true if using HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(maxAge);
        return jwtCookie;
    }
}
