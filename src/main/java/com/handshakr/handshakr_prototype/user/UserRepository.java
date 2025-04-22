package com.handshakr.handshakr_prototype.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link User} entities.
 * Extends {@link CrudRepository} to provide basic and custom user queries.
 */
@Repository
public interface UserRepository extends CrudRepository<User,Long> {

    /**
     * Finds a user by their unique username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user exists by username.
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether a user exists by email.
     */
    boolean existsByEmail(String username);

    /**
     * Retrieves all users in the database.
     */
    @Override
    List<User> findAll();

}
