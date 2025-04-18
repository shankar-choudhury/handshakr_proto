package com.handshakr.handshakr_prototype.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {
    @GetMapping("/debug/path")
    @PreAuthorize("permitAll()")
    public String debugPath(HttpServletRequest request) {
        return "Request reached: " + request.getRequestURI();
    }
}
