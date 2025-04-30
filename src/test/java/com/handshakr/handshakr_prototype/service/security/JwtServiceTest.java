package com.handshakr.handshakr_prototype.service.security;

import com.handshakr.handshakr_prototype.security.auth.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    private final String secretKey = Base64.getEncoder().encodeToString(
            Jwts.SIG.HS256.key().build().getEncoded()
    );
    private final long expiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        jwtService.setSecretKey(secretKey);
    }

    @Test
    void generateToken_ValidUserDetails_ReturnsToken() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("pass")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("pass")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_ExpiredToken_ReturnsFalse() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("pass")
                .roles("USER")
                .build();

        // Generate token with immediate expiration
        String token = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis() - 1000))
                .expiration(new Date(System.currentTimeMillis() - 500))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .compact();

        assertFalse(jwtService.isTokenValid(token, user));
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("pass")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void getExpirationTime_ReturnsConfiguredValue() {
        assertEquals(86400000, jwtService.getExpirationTime());
    }
}
