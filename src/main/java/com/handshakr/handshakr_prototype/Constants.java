package com.handshakr.handshakr_prototype;

public interface Constants {

    int COOKIE_EXPIRATION = 3600000; // 1 hour in milliseconds
    int JWT_EXPIRATION = 86400000; // 1 day in milliseconds
    String JWT_COOKIE_NAME = "jwtCookie";
    String CSRF_COOKIE_NAME =  "XSRF-TOKEN";

}