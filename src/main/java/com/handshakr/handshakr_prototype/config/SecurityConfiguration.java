package com.handshakr.handshakr_prototype.config;

import static com.handshakr.handshakr_prototype.auth.Constants.*;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfiguration(AuthenticationProvider authProvider, JwtAuthenticationFilter jwtFilter) {
        this.authProvider = authProvider;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                // CSRF with logout protection configuration
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .ignoringRequestMatchers("/auth/login", "/auth/register", "/jenkins")
                )

                // CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Authorization
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/register", "/auth/login", "/auth/logout", "/jenkins").permitAll()
                        .anyRequest().authenticated())

                // Stateless session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionFixation().none())

                // Filter order configuration
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new CsrfCookieResponseFilter(), CsrfFilter.class)

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .deleteCookies(JWT_COOKIE_NAME, CSRF_COOKIE_NAME))

                // Auth provider
                .authenticationProvider(authProvider)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        //config.setAllowedOrigins(List.of("*")); //List.of("http://localhost:8080", "http://localhost:3000", "https://handshakr-v2.vercel.app", "https://handshakr.duckdns.org"));

        // Web origins (SameSite=Lax)
        config.setAllowedOrigins(List.of(
                "https://handshakr.duckdns.org",
                "https://handshakr-v2.vercel.app",
                "http://localhost:3000"
        ));

        // Mobile patterns (SameSite=None)
        config.setAllowedOriginPatterns(List.of(
                "app://*",
                "http://localhost*"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);  // Crucial for cookies
        config.setExposedHeaders(List.of("X-CSRF-TOKEN"));  // Expose CSRF header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();

        repository.setCookieCustomizer(responseCookieBuilder -> responseCookieBuilder
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(-1)
        );

        return repository;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            ResponseCookie clearJwt = ResponseCookie.from(JWT_COOKIE_NAME, "")
                    .httpOnly(true).secure(true).path("/").sameSite("None").maxAge(0).build();

            ResponseCookie clearCsrf = ResponseCookie.from(CSRF_COOKIE_NAME, "")
                    .httpOnly(false).secure(true).path("/").sameSite("None").maxAge(0).build();

            response.addHeader(HttpHeaders.SET_COOKIE, clearJwt.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, clearCsrf.toString());
            response.setStatus(HttpStatus.OK.value());
        };
    }
}
