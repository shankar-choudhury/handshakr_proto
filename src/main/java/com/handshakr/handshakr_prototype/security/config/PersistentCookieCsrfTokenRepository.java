package com.handshakr.handshakr_prototype.security.config;

import com.handshakr.handshakr_prototype.Constants;
import com.handshakr.handshakr_prototype.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PersistentCookieCsrfTokenRepository implements CsrfTokenRepository {
    private static final String CSRF_COOKIE_NAME = Constants.CSRF_COOKIE_NAME;
    private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return loadTokenValue(request)
                .map(existingToken -> new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_COOKIE_NAME, existingToken))
                .orElse(new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_COOKIE_NAME, UUID.randomUUID().toString()));

    }

    @Override
    public void saveToken(
            CsrfToken token,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (loadTokenValue(request).isEmpty() && token != null && token.getToken() != null) {
            ResponseCookie cookie = CookieUtils
                    .createSecureCookie(
                            CSRF_COOKIE_NAME,
                            token.getToken(),
                            false,
                            -1);

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        CsrfToken token = loadTokenValue(request)
                .map(tk -> new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_COOKIE_NAME, tk))
                .orElse(null);

        if (token != null) {
            System.out.println("Loaded CSRF token from cookie: " + token.getToken());
        } else {
            System.out.println("No CSRF token found in cookies");
        }

        return token;
    }

    private Optional<String> loadTokenValue(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> Objects.equals(CSRF_COOKIE_NAME, cookie.getName()))
                .findFirst()
                .map(Cookie::getValue);
    }
}
