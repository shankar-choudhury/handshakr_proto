package com.handshakr.handshakr_prototype.auth;

import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public UserDetails authenticate(LoginRequest request) {
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                ));

        return (UserDetails) authentication.getPrincipal();
    }
}
