package com.handshakr.handshakr_prototype.auth;

import com.handshakr.handshakr_prototype.exceptions.ExceptionFactory;
import com.handshakr.handshakr_prototype.exceptions.types.BadRequestException;
import com.handshakr.handshakr_prototype.exceptions.types.ExceptionType;
import com.handshakr.handshakr_prototype.exceptions.types.UserAlreadyExistsException;
import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final ExceptionFactory exceptionFactory;

    public AuthServiceImpl(UserRepository repo, PasswordEncoder encoder, AuthenticationManager manager, ExceptionFactory exceptionFactory) {
        this.repo = repo;
        this.encoder = encoder;
        this.manager = manager;
        this.exceptionFactory = exceptionFactory;
    }

    @Override
    public User register(RegisterRequest request) {
        // Check if username already exists
        if (repo.existsByUsername(request.username())) {
            throw exceptionFactory.create(
                    ExceptionType.USER_ALREADY_EXISTS,
                    request.username());
        }

        // Check if email already exists
        if (repo.existsByEmail(request.email())) {
            throw exceptionFactory.create(
                    ExceptionType.USER_ALREADY_EXISTS,
                    request.email());
        }

        // Validate password strength
        if (request.password().length() < 8) {
            throw exceptionFactory.badRequest(
                    "Password must be at least 8 characters long");
        }

        try {
            return repo.save(
                    new User(
                            request.username(),
                            request.email(),
                            encoder.encode(request.password())
                    ));
        } catch (DataIntegrityViolationException e) {
            throw exceptionFactory.badRequest(
                    "Failed to register user: " + e.getRootCause().getMessage());
        }
    }

    @Override
    public UserDetails authenticate(LoginRequest request) {
        // Validate input
        if (request.username() == null || request.username().isBlank()) {
            throw exceptionFactory.badRequest("Username is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw exceptionFactory.badRequest("Password is required");
        }

        try {
            Authentication authentication = manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    ));
            return (UserDetails) authentication.getPrincipal();

        } catch (BadCredentialsException e) {
            throw exceptionFactory.invalidCredentials();

        } catch (DisabledException e) {
            throw exceptionFactory.create(
                    ExceptionType.ACCOUNT_DISABLED);

        } catch (LockedException e) {
            throw exceptionFactory.badRequest(
                    "Your account has been locked. Please contact support.");

        } catch (AuthenticationServiceException e) {
            throw exceptionFactory.badRequest(
                    "Authentication service unavailable. Please try again later.");
        }
    }
}
