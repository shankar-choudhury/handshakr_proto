package com.handshakr.handshakr_prototype.utils;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {
    public static ResponseCookie createSecureCookie(
            String name,
            String value,
            boolean httpOnly,
            int maxAgeSeconds
    ) {
        return ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite("Lax")
                .build();
    }
}
