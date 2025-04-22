package com.handshakr.handshakr_prototype.user;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Service interface for handling business logic related to {@link User} entities.
 */
public interface UserService {

    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return the found user
     */
    User findByUsername(String username);

    /**
     * Finds a user by their email.
     * @param email the email to search for
     * @return the found user
     */
    User findByEmail(String email);

    /**
     * Checks if a username is already taken.
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean usernameExists(String username);

    /**
     * Checks if an email is already used.
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean emailExists(String email);

    /**
     * Retrieves a list of all usernames in the system.
     * @return list of usernames
     */
    List<String> users();

    /**
     * Saves a new or existing user.
     * @param user the user to save
     * @return the saved user
     */
    User saveUser(User user);
}
