package com.handshakr.handshakr_prototype.user;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    List<String> users();
    User saveUser(User user);
}
