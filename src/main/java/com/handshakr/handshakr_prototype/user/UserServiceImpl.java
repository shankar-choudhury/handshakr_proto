package com.handshakr.handshakr_prototype.user;

import com.handshakr.handshakr_prototype.exceptions.ExceptionFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ExceptionFactory exceptionFactory;

    public UserServiceImpl(UserRepository userRepository, ExceptionFactory exceptionFactory) {
        this.userRepository = userRepository;
        this.exceptionFactory = exceptionFactory;
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw exceptionFactory.badRequest("Username cannot be empty");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> exceptionFactory.userNotFound(username));
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw exceptionFactory.badRequest("Email cannot be empty");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> exceptionFactory.userNotFound(email));
    }

    @Override
    public List<String> users() {
        try {
            return userRepository.findAll()
                    .stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw exceptionFactory.serviceUnavailable("Failed to retrieve user list: " + e.getMessage());
        }
    }

    @Override
    public boolean usernameExists(String username) {
        if (username == null || username.isBlank()) {
            throw exceptionFactory.badRequest("Username cannot be empty");
        }
        try {
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            throw exceptionFactory.databaseError("Failed to check username existence");
        }
    }

    @Override
    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            throw exceptionFactory.badRequest("Email cannot be empty");
        }
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            throw exceptionFactory.databaseError("Failed to check email existence");
        }
    }

    @Override
    public User saveUser(User user) {
        if (user == null) {
            throw exceptionFactory.badRequest("User cannot be null");
        }

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw exceptionFactory.databaseError("Database constraint violation: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            throw exceptionFactory.databaseError("Failed to save user: " + e.getMessage());
        }
    }
}
