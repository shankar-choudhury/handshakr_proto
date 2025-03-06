package com.example.demo.auth;

import com.example.demo.user.LoginRequest;
import com.example.demo.user.RegisterRequest;
import com.example.demo.user.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    User register(RegisterRequest request);
    UserDetails authenticate(LoginRequest request);
}
