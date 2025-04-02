package com.handshakr.handshakr_prototype.auth;

import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    User register(RegisterRequest request);
    UserDetails authenticate(LoginRequest request);
}
