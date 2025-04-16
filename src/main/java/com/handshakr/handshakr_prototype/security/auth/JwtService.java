package com.handshakr.handshakr_prototype.security.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {
    String extractUsername(String token);
    String generateToken(UserDetails details);
    String generateToken(Map<String,Object> extraClaims, UserDetails details);
    long getExpirationTime();
    boolean isTokenValid(String token, UserDetails details);

}
