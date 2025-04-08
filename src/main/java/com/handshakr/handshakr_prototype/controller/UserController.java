package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.handshake.HandshakeService;
import com.handshakr.handshakr_prototype.handshake.dto.CreateHandshakeRequest;
import com.handshakr.handshakr_prototype.handshake.dto.HandshakeDto;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.dto.SetPublicKeyRequest;
import com.handshakr.handshakr_prototype.user.dto.UserDto;
import com.handshakr.handshakr_prototype.user.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<List<String>>> getAllUsernames() {
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userService.users()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getAuthenticatedUser(Principal principal) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(ApiResponse.success("User details retrieved", UserDto.from(user)));
    }

    @GetMapping("/me/getPublicKey")
    public ResponseEntity<ApiResponse<String>> getPublicKey(Principal principal) {
        String publicKey = userService.findByUsername(principal.getName()).getPublicKey();
        return ResponseEntity.ok(ApiResponse.success("Public key retrieved", publicKey));
    }

    @PostMapping("/me/setPublicKey")
    public ResponseEntity<ApiResponse<Void>> setPublicKey(@Valid @RequestBody SetPublicKeyRequest request,
                                                          Principal principal) {
        User user = userService.findByUsername(principal.getName());
        user.setPublicKey(request.publicKey());
        userService.saveUser(user);
        return ResponseEntity.ok(ApiResponse.success("Public key updated successfully"));
    }

    @PostMapping("/create-handshake")
    public ResponseEntity<ApiResponse<HandshakeDto>> createHandshakeAsInitiator(
            @Valid @RequestBody CreateHandshakeRequest request,
            Principal principal) {

        System.out.println(request);

        String initiatorUsername = principal.getName();
        User initiator = userService.findByUsername(initiatorUsername);
        User acceptor = userService.findByUsername(request.receiverUsername());

        handshakeService.createHandshake(request, initiatorUsername);
        HandshakeDto createdHandshake = handshakeService.getHandshakeByName(request.handshakeName());

        return ResponseEntity.ok(ApiResponse.success("Handshake created successfully", createdHandshake));
    }
}
