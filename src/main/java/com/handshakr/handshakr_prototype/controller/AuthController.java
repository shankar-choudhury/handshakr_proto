package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.auth.AuthService;
import com.handshakr.handshakr_prototype.auth.JwtService;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import com.handshakr.handshakr_prototype.user.LoginRequest;
import com.handshakr.handshakr_prototype.user.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.handshakr.handshakr_prototype.auth.Constants.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final CsrfTokenRepository csrfTokenRepository;

    public AuthController(AuthService authService,
                          JwtService jwtService,
                          UserDetailsService userDetailsService,
                          CsrfTokenRepository csrfTokenRepository) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@ModelAttribute RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@ModelAttribute LoginRequest loginRequest, HttpServletRequest httpRequest, HttpServletResponse response) {
        authService.authenticate(loginRequest);

        UserDetails details = userDetailsService.loadUserByUsername(loginRequest.username());

        String jwtToken = jwtService.generateToken(details);
        Cookie jwtCookie = createCookie(JWT_COOKIE_NAME, jwtToken, true, COOKIE_EXPIRATION);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(httpRequest);
        Cookie csrfCookie = createCookie(CSRF_COOKIE_NAME, csrfToken.getToken(), false, COOKIE_EXPIRATION);

        response.addCookie(jwtCookie);
        response.addCookie(csrfCookie);

        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        // Invalidate cookie given by login by having browser overwrite cookie with same name
        response.addCookie(createCookie(JWT_COOKIE_NAME, null, true, 0));
        response.addCookie(createCookie(CSRF_COOKIE_NAME, null, false, 0));

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An error occurred: " + ex.getMessage(), 500));
    }

    private Cookie createCookie(String cookieName, String token, boolean httpOnly, int maxAge) {
        Cookie jwtCookie = new Cookie(cookieName, token);
        jwtCookie.setHttpOnly(httpOnly);
        jwtCookie.setSecure(false); //Set to true if using HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(maxAge);
        return jwtCookie;
    }
}
