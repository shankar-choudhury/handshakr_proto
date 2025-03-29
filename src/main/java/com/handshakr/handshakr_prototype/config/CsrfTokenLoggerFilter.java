package com.handshakr.handshakr_prototype.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class CsrfTokenLoggerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

        if (token != null) {
            System.out.println("CSRF Token: " + token.getToken());
            System.out.println("CSRF Header: " + request.getHeader("X-XSRF-TOKEN"));
            System.out.println("CSRF Cookie: " + Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("XSRF-TOKEN"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse("MISSING"));
        }

        filterChain.doFilter(request, response);
    }
}
