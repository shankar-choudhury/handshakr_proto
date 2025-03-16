package com.handshakr.handshakr_prototype.auth;

import com.handshakr.handshakr_prototype.user.LoginRequest;
import com.handshakr.handshakr_prototype.user.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    User register(RegisterRequest request);
    UserDetails authenticate(LoginRequest request);
}
