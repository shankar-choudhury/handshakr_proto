package com.handshakr.handshakr_prototype.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * A filter that sets the CSRF token as a response header and exposes the XSRF-TOKEN cookie value in the request attributes.
 *
 * This is an insecure practice, as it defeats the purpose of JWT Cookie + CSRF double-submit cookie security pattern. The motivation for this was to aid in frontend and mobile development so that cookie values and token values are available and easily integrated.
 *
 * <p>This filter should be placed after the {@link org.springframework.security.web.csrf.CsrfFilter}.
 */
public class CsrfCookieResponseFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

        if (csrfToken != null) {
            response.setHeader("X-CSRF-TOKEN", csrfToken.getToken());

            Collection<String> cookies = response.getHeaders(HttpHeaders.SET_COOKIE);

            cookies.stream()
                    .filter(c -> c.startsWith("XSRF-TOKEN="))
                    .findFirst()
                    .ifPresent(c -> request.setAttribute("XSRF_TOKEN", c));
        }
        filterChain.doFilter(request, response);
    }
}
