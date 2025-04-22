package com.handshakr.handshakr_prototype.utils;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

/**
 * Utility class for creating secure HTTP cookies.
 */
public class CookieUtils {

    /**
     * Creates a secure {@link ResponseCookie} with the provided properties.
     * <p>
     * This method sets the following defaults:
     * <ul>
     *     <li>Secure flag: true (sent over HTTPS only)</li>
     *     <li>Path: "/" (available to the entire application)</li>
     *     <li>SameSite: "None" (allows cross-site usage, e.g., for third-party apps)</li>
     * </ul>
     *
     * @param name           the name of the cookie
     * @param value          the value of the cookie
     * @param httpOnly       whether the cookie is HTTP-only (inaccessible to JavaScript)
     * @param maxAgeSeconds  the maximum age of the cookie in seconds
     * @return a configured {@link ResponseCookie}
     */
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
                .sameSite("None")
                .build();
    }
}
