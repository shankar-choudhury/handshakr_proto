package com.handshakr.handshakr_prototype.auth;

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

@Service
public class AuthServiceImpl implements AuthService{
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final UserExceptionFactory userExceptionFactory;

    public AuthServiceImpl(UserService userService, PasswordEncoder encoder, AuthenticationManager manager, UserExceptionFactory userExceptionFactory) {
        this.userService = userService;
        this.encoder = encoder;
        this.manager = manager;
        this.userExceptionFactory = userExceptionFactory;
    }

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

    @Override
    public UserDetails authenticate(LoginRequest request) {
        // Validate input
        if (request.username() == null || request.username().isBlank() || request.username().isEmpty()) {
            throw userExceptionFactory.badRequest("Username is required");
        }
        if (request.password() == null || request.password().isBlank() || request.password().isEmpty()) {
            throw userExceptionFactory.badRequest("Password is required");
        }

        try {
            Authentication authentication = manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    ));
            return (UserDetails) authentication.getPrincipal();

        } catch (BadCredentialsException e) {
            throw userExceptionFactory.invalidCredentials();

        } catch (DisabledException e) {
            throw userExceptionFactory.create(
                    UserExceptionType.ACCOUNT_DISABLED);

        } catch (LockedException e) {
            throw userExceptionFactory.badRequest(
                    "Your account has been locked. Please contact support.");

        } catch (AuthenticationServiceException e) {
            throw userExceptionFactory.badRequest(
                    "Authentication service unavailable. Please try again later.");
        }
    }
}
