package com.handshakr.handshakr_prototype.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Log request method and URL
        System.out.println("""


                """);
        System.out.println("=== Printing Out Incoming Request ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL().toString());

        // Log headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        System.out.println("Headers: + \n");
        for (Map.Entry<String, String> h : headers.entrySet()) {
            System.out.println("Header: " + h.getKey() + "| Value: " + h.getValue());
        }

        System.out.println("Cookies: + \n");
        // Log cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println("Cookie name: " + cookie.getName() + ", Cookie value: " + cookie.getValue());
            }
        }
        System.out.println("=== End of Printed Incoming Request, Passing Through Security Filters ===");
        System.out.println("""


                """);


        filterChain.doFilter(request, response);
    }
}
