package com.example.demo.auth;

import com.example.demo.user.LoginRequest;
import com.example.demo.user.RegisterRequest;
import com.example.demo.user.User;

public interface AuthService {
    User register(RegisterRequest request);
    String authenticate(LoginRequest request);
}
