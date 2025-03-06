package com.example.demo.auth;

public interface Constants {

    // Cookie Expiration (in milliseconds)
    public static final int COOKIE_EXPIRATION = 86400000; // 1 day in milliseconds

    // JWT Expiration (in milliseconds)
    public static final int JWT_EXPIRATION = 86400000; // 1 day in milliseconds

    // Cookie Name
    public static final String COOKIE_NAME = "JWT";
}