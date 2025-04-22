package com.handshakr.handshakr_prototype.security.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Service interface for handling JWT (JSON Web Token) creation, validation, and parsing.
 */
public interface JwtService {
    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username stored in the token
     */
    String extractUsername(String token);

    /**
     * Generates a JWT token for the given user details.
     *
     * @param details the user details
     * @return a signed JWT token
     */
    String generateToken(UserDetails details);

    /**
     * Generates a JWT token for the given user details and includes additional claims.
     *
     * @param extraClaims a map of additional claims to embed in the token
     * @param details the user details
     * @return a signed JWT token
     */
    String generateToken(Map<String,Object> extraClaims, UserDetails details);

    /**
     * Returns the configured JWT expiration time in milliseconds.
     *
     * @return the expiration time in ms
     */
    long getExpirationTime();

    /**
     * Validates whether the JWT token belongs to the given user and is not expired.
     *
     * @param token the JWT token
     * @param details the user details
     * @return true if the token is valid and not expired; false otherwise
     */
    boolean isTokenValid(String token, UserDetails details);

}
