package com.handshakr.handshakr_prototype.security.auth;

import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for handling authentication-related operations such as
 * user registration and login authentication.
 */
public interface AuthService {
    /**
     * Registers a new user based on the provided registration details.
     *
     * @param request the registration request containing username, email, and password
     * @return the newly created {@link User} entity
     * @throws com.handshakr.handshakr_prototype.exceptions.user.UserAlreadyExistsException
     *         if the username or email already exists
     * @throws com.handshakr.handshakr_prototype.exceptions.general.ValidationException
     *         if the password does not meet minimum requirements
     */
    User register(RegisterRequest request);

    /**
     * Authenticates a user using their login credentials.
     *
     * @param request the login request containing username and password
     * @return the authenticated {@link UserDetails}
     * @throws org.springframework.security.authentication.BadCredentialsException
     *         if the provided credentials are incorrect
     * @throws org.springframework.security.authentication.DisabledException
     *         if the user account is disabled
     * @throws org.springframework.security.authentication.LockedException
     *         if the user account is locked
     */
    UserDetails authenticate(LoginRequest request);
}
