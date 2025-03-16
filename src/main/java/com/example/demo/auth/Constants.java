package com.example.demo.auth;

public interface Constants {

    // Cookie Expiration (in milliseconds)
    int COOKIE_EXPIRATION = 3600000; // 1 day in milliseconds

    // JWT Expiration (in milliseconds)
    int JWT_EXPIRATION = 86400000; // 1 day in milliseconds

    // Cookie Name
    String COOKIE_NAME = "jwtCookie";
}