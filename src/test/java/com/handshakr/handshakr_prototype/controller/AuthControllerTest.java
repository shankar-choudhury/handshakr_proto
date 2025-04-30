package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.exceptions.user.AccountLockedException;
import com.handshakr.handshakr_prototype.security.auth.AuthService;
import com.handshakr.handshakr_prototype.security.auth.JwtService;
import com.handshakr.handshakr_prototype.security.SecurityConfiguration;
import com.handshakr.handshakr_prototype.security.filter.JwtAuthenticationFilter;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({AuthControllerTest.TestSecurityConfig.class, AuthControllerTest.MockAuthBeans.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtFilter;

    // ===== TEST CONFIGURATIONS =====
    @TestConfiguration
    class MockAuthBeans {
        @Bean
        public AuthenticationProvider authenticationProvider() {
            return mock(AuthenticationProvider.class);
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationProvider provider) {
            return provider::authenticate;
        }
    }

    @TestConfiguration
    class TestSecurityConfig extends SecurityConfiguration {

        public TestSecurityConfig(AuthenticationProvider authProvider, JwtAuthenticationFilter jwtFilter) {
            super(authProvider, jwtFilter);
        }

        @Override
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // Simplified security configuration for testing
            return http
                    .csrf(csrf -> csrf
                            .csrfTokenRepository(testCsrfTokenRepository())
                            .ignoringRequestMatchers("/auth/**", "/debug/**")
                    )
                    .cors(cors -> cors.disable()) // Disable CORS for tests
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

        @Bean
        public CsrfTokenRepository testCsrfTokenRepository() {
            CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
            repository.setCookiePath("/");
            return repository;
        }
    }

    // ===== REGISTER TESTS =====
    @Test
    void register_ValidRequest_ReturnsSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "valid@email.com", "Password123!");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    // ===== LOGIN TESTS =====
    @Test
    void login_ValidCredentials_ReturnsJwtAndCsrf() throws Exception {
        LoginRequest request = new LoginRequest("user", "Password123!");
        UserDetails userDetails = new User("user", "encodedPass", "user@test.com");
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(authService.authenticate(any())).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("mock.jwt.token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.data.jwtCookie").exists())
                .andExpect(jsonPath("$.data.csrfCookie").exists());
    }

    // ===== SECURITY INTEGRATION TESTS =====
    @Test
    void validateToken_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/auth/validate"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    void validateToken_Authenticated_ReturnsUsername() throws Exception {
        mockMvc.perform(get("/auth/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("testUser"));
    }

    // ===== ERROR SCENARIOS =====
    @Test
    void login_LockedAccount_Returns403() throws Exception {
        LoginRequest request = new LoginRequest("lockedUser", "password");

        when(authService.authenticate(any()))
                .thenThrow(new AccountLockedException("Account locked"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isForbidden());
    }

    // ===== HELPER METHODS =====
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}