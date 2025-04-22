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

/**
 * Controller for handling user-related operations and information.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final HandshakeService handshakeService;

    /**
     * Constructs a UserController with required services.
     *
     * @param userService the user management service
     * @param handshakeService the handshake management service
     */
    public UserController(UserService userService, HandshakeService handshakeService) {
        this.userService = userService;
        this.handshakeService = handshakeService;
    }

    /**
     * Retrieves all registered usernames.
     *
     * @return response containing list of usernames
     */
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<String>>> getAllUsernames() {
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userService.users()));
    }

    /**
     * Retrieves details of the currently authenticated user.
     *
     * @param principal the current user principal
     * @return response containing user details
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getAuthenticatedUser(Principal principal) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(ApiResponse.success("User details retrieved", UserDto.from(user)));
    }

    /**
     * Retrieves the public key of the currently authenticated user.
     *
     * @param principal the current user principal
     * @return response containing the public key
     */
    @GetMapping("/me/getPublicKey")
    public ResponseEntity<ApiResponse<String>> getPublicKey(Principal principal) {
        String publicKey = userService.findByUsername(principal.getName()).getPublicKey();
        return ResponseEntity.ok(ApiResponse.success("Public key retrieved", publicKey));
    }

    /**
     * Sets or updates the public key for the currently authenticated user.
     *
     * @param request the new public key data
     * @param principal the current user principal
     * @return response indicating success
     */
    @PostMapping("/me/setPublicKey")
    public ResponseEntity<ApiResponse<Void>> setPublicKey(@Valid @RequestBody SetPublicKeyRequest request,
                                                          Principal principal) {
        User user = userService.findByUsername(principal.getName());
        user.setPublicKey(request.publicKey());
        userService.saveUser(user);
        return ResponseEntity.ok(ApiResponse.success("Public key updated successfully"));
    }

    /**
     * Creates a new handshake where the authenticated user is the initiator.
     *
     * @param request the handshake creation request
     * @param principal the current user principal
     * @return response containing the created handshake
     */
    @PostMapping("/create-handshake")
    public ResponseEntity<ApiResponse<HandshakeDto>> createHandshakeAsInitiator(
            @Valid @RequestBody CreateHandshakeRequest request,
            Principal principal) {

        String initiatorUsername = principal.getName();
        User initiator = userService.findByUsername(initiatorUsername);
        User acceptor = userService.findByUsername(request.receiverUsername());

        handshakeService.createHandshake(request, initiatorUsername);
        HandshakeDto createdHandshake = handshakeService.getHandshakeByName(request.handshakeName());

        return ResponseEntity.ok(ApiResponse.success("Handshake created successfully", createdHandshake));
    }
}
