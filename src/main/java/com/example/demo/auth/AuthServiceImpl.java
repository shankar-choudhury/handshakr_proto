package com.example.demo.auth;

import com.example.demo.user.LoginRequest;
import com.example.demo.user.RegisterRequest;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;

    public AuthServiceImpl(UserRepository repo, PasswordEncoder encoder, AuthenticationManager manager) {
        this.repo = repo;
        this.encoder = encoder;
        this.manager = manager;
    }

    @Override
    public User register(RegisterRequest request) {
        return repo.save(
                new User(
                        request.username(),
                        request.email(),
                        encoder.encode(request.password()))
        );
    }

    @Override
    public String authenticate(LoginRequest request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                ));

        return request.username() + "successfully logged in";
    }
}
