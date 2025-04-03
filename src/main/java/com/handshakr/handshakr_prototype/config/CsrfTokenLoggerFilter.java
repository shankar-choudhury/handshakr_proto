package com.handshakr.handshakr_prototype.config;

import com.handshakr.handshakr_prototype.auth.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

            // 1. Print expected token (server-side)
            System.out.println("[Server] Expected Token: " + token.getToken());

            // 2. Print received token (from client request)
            String receivedToken = request.getHeader("X-CSRF-TOKEN"); // Check header
            if (receivedToken == null) {
                receivedToken = request.getParameter("_csrf"); // Check parameter
            }
            System.out.println("[Client] Received Token: " +
                    (receivedToken != null ? receivedToken : "NOT FOUND"));

            // 3. Print cookie value (if available)
            if (request.getCookies() != null) {
                Arrays.stream(request.getCookies())
                        .filter(c -> c.getName().equals(Constants.CSRF_COOKIE_NAME))
                        .findFirst()
                        .ifPresentOrElse(
                                c -> System.out.println("[Cookie] CSRF Token: " + c.getValue()),
                                () -> System.out.println("[Cookie] CSRF Token: MISSING")
                        );
            } else {
                System.out.println("[Cookie] No cookies in request");
            }

            System.out.println("======================\n");
        }

        filterChain.doFilter(request, response);
    }
}
