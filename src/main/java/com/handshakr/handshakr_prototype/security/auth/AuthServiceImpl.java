package com.handshakr.handshakr_prototype.security.auth;

import com.handshakr.handshakr_prototype.exceptions.UserExceptionFactory;
import com.handshakr.handshakr_prototype.exceptions.user.UserExceptionType;
import com.handshakr.handshakr_prototype.user.UserService;
import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link AuthService} interface that handles user
 * registration and authentication using Spring Security components.
 */
@Service
public class AuthServiceImpl implements AuthService{
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final UserExceptionFactory userExceptionFactory;

    /**
     * Constructs a new {@code AuthServiceImpl}.
     *
     * @param userService the service for managing users
     * @param encoder the password encoder for securing passwords
     * @param manager the authentication manager for verifying credentials
     * @param userExceptionFactory the factory for creating user-related exceptions
     */
    public AuthServiceImpl(UserService userService, PasswordEncoder encoder, AuthenticationManager manager, UserExceptionFactory userExceptionFactory) {
        this.userService = userService;
        this.encoder = encoder;
        this.manager = manager;
        this.userExceptionFactory = userExceptionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User register(RegisterRequest request) {
        // Check if username already exists
        if (userService.usernameExists(request.username())) {
            throw userExceptionFactory.create(
                    UserExceptionType.USER_ALREADY_EXISTS,
                    request.username());
        }

        // Check if email already exists
        if (userService.emailExists(request.email())) {
            throw userExceptionFactory.create(
                    UserExceptionType.USER_ALREADY_EXISTS,
                    request.email());
        }

        // Validate password strength
        if (request.password().length() < 8) {
            throw userExceptionFactory.badRequest(
                    "Password must be at least 8 characters long");
        }

        try {
            return userService.saveUser(
                    new User(
                            request.username(),
                            request.email(),
                            encoder.encode(request.password())
                    ));
        } catch (DataIntegrityViolationException e) {
            throw userExceptionFactory.badRequest(
                    "Failed to register user: " + e.getRootCause().getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails authenticate(LoginRequest request) {
        try {
            Authentication authentication = manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    ));
            return (UserDetails) authentication.getPrincipal();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        } catch (DisabledException e) {
            throw new DisabledException("Account is disabled");
        } catch (LockedException e) {
            throw new LockedException("Account is locked");
        }
    }
}
