package com.handshakr.handshakr_prototype.user;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    List<String> users();
}
