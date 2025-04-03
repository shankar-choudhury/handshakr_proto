package com.handshakr.handshakr_prototype.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfDebugController {
    @GetMapping("/csrf-debug")
    public String debugCsrf(@RequestHeader(value = "X-XSRF-TOKEN", required = false) String headerToken,
                            HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        return "Header: " + headerToken + "\nAttribute: " + (token != null ? token.getToken() : "null");
    }
}