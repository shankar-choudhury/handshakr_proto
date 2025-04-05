package com.handshakr.handshakr_prototype.user;

import com.handshakr.handshakr_prototype.exceptions.UserExceptionFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserExceptionFactory userExceptionFactory;

    public UserServiceImpl(UserRepository userRepository, UserExceptionFactory userExceptionFactory) {
        this.userRepository = userRepository;
        this.userExceptionFactory = userExceptionFactory;
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw userExceptionFactory.badRequest("Username cannot be empty");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> userExceptionFactory.userNotFound(username));
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw userExceptionFactory.badRequest("Email cannot be empty");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> userExceptionFactory.userNotFound(email));
    }

    @Override
    public List<String> users() {
        try {
            return userRepository.findAll()
                    .stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw userExceptionFactory.serviceUnavailable("Failed to retrieve user list: " + e.getMessage());
        }
    }

    @Override
    public boolean usernameExists(String username) {
        if (username == null || username.isBlank()) {
            throw userExceptionFactory.badRequest("Username cannot be empty");
        }
        try {
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            throw userExceptionFactory.databaseError("Failed to check username existence");
        }
    }

    @Override
    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            throw userExceptionFactory.badRequest("Email cannot be empty");
        }
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            throw userExceptionFactory.databaseError("Failed to check email existence");
        }
    }

    @Override
    public User saveUser(User user) {
        if (user == null) {
            throw userExceptionFactory.badRequest("User cannot be null");
        }

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw userExceptionFactory.databaseError("Database constraint violation: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            throw userExceptionFactory.databaseError("Failed to save user: " + e.getMessage());
        }
    }
}
