package com.handshakr.handshakr_prototype.config;

import com.handshakr.handshakr_prototype.auth.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class CsrfTokenLoggerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

        if (token != null) {
            System.out.println("\n=== CSRF Token Debug ===");
            System.out.println("Token value: " + token.getToken());
            System.out.println("Header received: " + request.getHeader(token.getHeaderName()));

            if (request.getCookies() != null) {
                Arrays.stream(request.getCookies())
                        .filter(c -> c.getName().equals(Constants.CSRF_COOKIE_NAME))
                        .findFirst()
                        .ifPresentOrElse(
                                c -> System.out.println("Cookie value: " + c.getValue()),
                                () -> System.out.println("CSRF Cookie MISSING")
                        );
            } else {
                System.out.println("No cookies in request");
            }
            System.out.println("======================\n");
        }

        filterChain.doFilter(request, response);
    }
}
