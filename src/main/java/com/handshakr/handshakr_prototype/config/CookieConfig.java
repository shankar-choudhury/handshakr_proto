package com.handshakr.handshakr_prototype.config;

import com.handshakr.handshakr_prototype.auth.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

//@Configuration
public class CookieConfig {

    // Define your web origins here (matches CORS config)
    private static final List<String> WEB_ORIGINS = List.of(
            "https://handshakr-v2.vercel.app",
            "http://localhost:3000"
    );

    //@Bean
    public CookieSameSiteSupplier sameSiteSupplier() {
        return cookie -> {
            if (Constants.CSRF_COOKIE_NAME.equals(cookie.getName())) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes()).getRequest();

                String origin = request.getHeader("Origin");
                boolean isWeb = isWebOrigin(request);
                System.out.println("CSRF Cookie - Origin: " + origin + " | SameSite: " + (isWeb ? "Lax" : "None"));

                return isWeb ? Cookie.SameSite.LAX : Cookie.SameSite.NONE;
            }
            return null;
        };
    }

    private Cookie.SameSite getSameSiteForRequest() {
        return Optional.of(RequestContextHolder.getRequestAttributes())
                .filter(attrs -> attrs instanceof ServletRequestAttributes)
                .map(attrs -> ((ServletRequestAttributes) attrs).getRequest())
                .map(this::isWebOrigin)
                .filter(isWeb -> !isWeb)
                .map(isWeb -> Cookie.SameSite.NONE)
                .orElse(Cookie.SameSite.LAX); // Default to secure behavior
    }

    private boolean isWebOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin == null) return true;

        return WEB_ORIGINS.stream()
                .anyMatch(origin::startsWith);
    }
}
