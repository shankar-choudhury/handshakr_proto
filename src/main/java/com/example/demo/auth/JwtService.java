package com.example.demo.auth;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    <T> T extractClaim(String token, Function<Claims,T> claimsResolver);
    String generateToken(UserDetails details);
    String generateToken(Map<String,Object> extraClaims, UserDetails details);
    long getExpirationTime();
    boolean isTokenValid(String token, UserDetails details);

}
