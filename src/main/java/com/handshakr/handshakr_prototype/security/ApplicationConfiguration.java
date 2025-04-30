package com.handshakr.handshakr_prototype.security;

import com.handshakr.handshakr_prototype.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

/**
 * Application-level configuration for authentication-related Spring Security beans.
 *
 * <p>This configuration defines beans for user details service, password encoding,
 * authentication manager, and authentication provider.</p>
 */
@Configuration
public class ApplicationConfiguration {
    private final UserService userService;

    /**
     * Constructs the ApplicationConfiguration with a provided UserService.
     *
     * @param userService the service used to retrieve user details
     */
    public ApplicationConfiguration(UserService userService) {
        this.userService = userService;
    }

    /**
     * Defines the {@link UserDetailsService} bean using the application's UserService.
     *
     * @return the user details service used to load users by username
     */
    @Bean
    UserDetailsService userDetailsService() {
        return userService::findByUsername;
    }

    /**
     * Defines the {@link PasswordEncoder} bean.
     *
     * <p>Uses PBKDF2 with Spring Security defaults as of version 5.8.</p>
     *
     * @return the password encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    /**
     * Defines the {@link AuthenticationManager} bean.
     *
     * <p>Delegates to Spring's internal authentication configuration.</p>
     *
     * @param config the Spring-provided authentication configuration
     * @return the authentication manager
     * @throws Exception in case of failure retrieving the manager
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{ return config.getAuthenticationManager();}

    /**
     * Defines the {@link AuthenticationProvider} bean.
     *
     * <p>Uses {@link DaoAuthenticationProvider} configured with the user details service
     * and password encoder defined above.</p>
     *
     * @return the authentication provider
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
