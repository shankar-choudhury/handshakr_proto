package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.security.auth.AuthService;
import com.handshakr.handshakr_prototype.security.auth.JwtService;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import com.handshakr.handshakr_prototype.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.handshakr.handshakr_prototype.Constants.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,String>>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        UserDetails userDetails = authService.authenticate(loginRequest);
        String jwtToken = jwtService.generateToken(userDetails);

        ResponseCookie jwtCookie = CookieUtils.createSecureCookie(
                JWT_COOKIE_NAME,
                jwtToken,
                true,
                COOKIE_EXPIRATION
        );

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        String xsrfCookieString = (String) request.getAttribute("XSRF_TOKEN");

        Map<String, String> data = new HashMap<>();
        data.put("jwtCookie", jwtCookie.toString());  // Optional: Include JWT cookie string

        if (xsrfCookieString != null) {
            // 4. Include the full XSRF-TOKEN cookie string in the response body
            data.put("csrfCookie", xsrfCookieString);
        }
        return ResponseEntity.ok(ApiResponse.success("Login successful", data));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Token valid", principal.getName()));
    }
}
