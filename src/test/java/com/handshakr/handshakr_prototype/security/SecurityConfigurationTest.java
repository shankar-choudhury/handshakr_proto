package com.handshakr.handshakr_prototype.security;

import com.handshakr.handshakr_prototype.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
@AutoConfigureMockMvc
@Import({SecurityConfigurationTest.TestSecurityConfig.class, SecurityConfigurationTest.MockBeansConfig.class})
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @TestConfiguration
    static class MockBeansConfig {
        @MockitoBean
        AuthenticationProvider authenticationProvider;

        @MockitoBean
        JwtAuthenticationFilter jwtAuthenticationFilter;
    }

    // ========== SECURITY FILTER CHAIN TESTS ==========
    @Test
    void securityFilterChain_WhenProtectedEndpoint_RequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void securityFilterChain_WhenAuthenticated_AllowsAccess() throws Exception {
        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isNotFound()); // 404 because route doesn't exist
    }

    // ========== CSRF CONFIGURATION TESTS ==========
    @Test
    void csrfConfiguration_WhenProtectedPostRequest_RequiresValidToken() throws Exception {
        mockMvc.perform(post("/api/protected"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/protected").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void csrfConfiguration_WhenAuthEndpoint_IgnoresCsrf() throws Exception {
        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isOk());
    }

    // ========== CORS CONFIGURATION TESTS ==========
    @Test
    void corsConfiguration_WhenAllowedOrigin_ReturnsCorsHeaders() throws Exception {
        mockMvc.perform(options("/auth/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void corsConfiguration_WhenDisallowedOrigin_RejectsRequest() throws Exception {
        mockMvc.perform(options("/auth/login")
                        .header("Origin", "http://evil.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    // ========== LOGOUT CONFIGURATION TESTS ==========
    @Test
    @WithMockUser
    void logoutConfiguration_WhenLogout_ClearsCookies() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertThat(response.getCookies())
                .extracting(Cookie::getName)
                .containsExactlyInAnyOrder("JWT", "XSRF-TOKEN");
    }

    // ========== SESSION CONFIGURATION TESTS ==========
    @Test
    void sessionManagement_IsStateless() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(cookie().doesNotExist("JSESSIONID"));
    }

    // ========== TEST CONFIGURATION ==========
    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig extends SecurityConfiguration {
        public TestSecurityConfig(AuthenticationProvider authProvider,
                                  JwtAuthenticationFilter jwtFilter) {
            super(authProvider, jwtFilter);
        }

        @Bean
        @Override
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf
                            .csrfTokenRepository(new PersistentCookieCsrfTokenRepository())
                            .ignoringRequestMatchers("/auth/**"))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/**").permitAll()
                            .anyRequest().authenticated())
                    .build();
        }

        @Bean
        @Override
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:3000"));
            config.setAllowedMethods(List.of("*"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }
    }
}
