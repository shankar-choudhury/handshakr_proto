package com.handshakr.handshakr_prototype.security.auth;

import com.handshakr.handshakr_prototype.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

/**
 * Implementation of {@link JwtService} that provides functionality for generating and validating JWT tokens.
 */
@Service
public class JwtServiceImpl implements JwtService{
    @Value("${jwt.secret-key}")
    private String secretKey;
    private final long jwtExpiration = Constants.JWT_EXPIRATION;

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(UserDetails details) {
        return generateToken(new HashMap<>(), details);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails details) {
        return buildToken(extraClaims, details, jwtExpiration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenValid(String token, UserDetails details) {
        String username = extractUsername(token);
        return (Objects.equals(username, details.getUsername())) && !isTokenExpired(token);
    }

    private String buildToken(
            Map<String,Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .claims()
                .add(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(getSignInKey())
                .compact();
    }

    private String generateBase64Key() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256);
            SecretKey key = keyGenerator.generateKey();
            String val = Base64.getEncoder().encodeToString(key.getEncoded());
            System.out.println(val);
            return val;
        } catch (NoSuchAlgorithmException na) {
            throw new RuntimeException("Error using key generation algo: algo does not exits", na);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date extractExpiration(String token) {return extractClaim(token, Claims::getExpiration);}


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
