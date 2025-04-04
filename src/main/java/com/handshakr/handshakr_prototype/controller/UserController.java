package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.handshake.HandshakeService;
import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.dto.SetPublicKeyRequest;
import com.handshakr.handshakr_prototype.user.dto.UserDto;
import com.handshakr.handshakr_prototype.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final HandshakeService handshakeService;

    public UserController(UserService userService, HandshakeService handshakeService) {
        this.userService = userService;
        this.handshakeService = handshakeService;
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> users() {
        return ResponseEntity.ok(userService.users());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> authenticatedUser(Principal principal) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(UserDto.from(user));
    }

    public ResponseEntity<String> getPublicKey(Principal principal) {
        String name = principal.getName();
        String publicKey = userService.findByUsername(name).getPublicKey();
        return ResponseEntity.ok(publicKey);
    }

    public ResponseEntity<HttpStatus> setPublicKey(@RequestBody SetPublicKeyRequest request, Principal principal) {
        String name = principal.getName();
        userService.findByUsername(name).setPublicKey(request.publicKey());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create-handshake")
    public ResponseEntity<HandshakeDto> createHandshakeAsInitiator(
            @RequestBody CreateHandshakeRequest request,
            Principal principal) {

        // Get logged-in user (initiator)
        String initiatorUsername = principal.getName();
        User initiator = userService.findByUsername(initiatorUsername);

        System.out.println("HandshakeController: initiator username: " + initiatorUsername);

        // Verify acceptor exists
        User acceptor = userService.findByUsername(request.acceptorUsername());

        System.out.println("HandshakeController: receiver username: " + acceptor.getUsername());

        // Create new request with initiator set to current user
        CreateHandshakeRequest securedRequest = new CreateHandshakeRequest(
                request.handshakeName(),
                request.encryptedDetails(),
                new Date(), // Use current time
                initiatorUsername,
                request.acceptorUsername()
        );

        handshakeService.createHandshake(securedRequest);
        HandshakeDto createdHandshake = handshakeService.getHandshakeByName(request.handshakeName());

        return ResponseEntity.ok(createdHandshake);
    }
}
