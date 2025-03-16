package com.handshakr.handshakr_prototype.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<String> users() {
        return repo.findAll().stream().map(User::getUsername).collect(Collectors.toList());
    }
}
