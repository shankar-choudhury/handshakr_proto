package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.auth.AuthService;
import com.handshakr.handshakr_prototype.auth.JwtService;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {
        System.out.println(request);
        User user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest, HttpServletResponse response) {
        UserDetails loginResponse = authService.authenticate(loginRequest);

        UserDetails details = userDetailsService.loadUserByUsername(loginRequest.username());

        String jwtToken = jwtService.generateToken(details);
        Cookie jwtCookie = createCookie(JWT_COOKIE_NAME, jwtToken, true, COOKIE_EXPIRATION);

        response.addCookie(jwtCookie);

        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtToken));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An error occurred: " + ex.getMessage() + "/n" + Arrays.toString(ex.getStackTrace()), 500));
    }

    private Cookie createCookie(String cookieName, String token, boolean httpOnly, int maxAge) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(false); //Set to true if using HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
