package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> users() {
        return ResponseEntity.ok(service.users());
    }

    @GetMapping("/current")
    public ResponseEntity<User> authenticatedUser() {
        var curr = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(curr);
    }
}
