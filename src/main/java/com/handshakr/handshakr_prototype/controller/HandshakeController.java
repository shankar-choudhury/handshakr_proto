package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.handshake.HandshakeService;
import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;
import com.handshakr.handshakr_prototype.handshake.dto.*;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for managing handshake lifecycle operations.
 */
@RestController
@RequestMapping("/handshake")
public class HandshakeController {
    private final HandshakeService handshakeService;

    /**
     * Constructs a HandshakeController with the handshake service.
     *
     * @param handshakeService the service managing handshake logic
     */
    public HandshakeController(HandshakeService handshakeService) {
        this.handshakeService = handshakeService;
    }

    /**
     * Retrieves a handshake by its name.
     *
     * @param handshakeName the name of the handshake
     * @return response containing handshake data
     */
    @GetMapping("/get-handshake-by-name/{handshakeName}")
    public ResponseEntity<ApiResponse<HandshakeDto>> getHandshakeByName(
            @PathVariable String handshakeName) {
        HandshakeDto handshake = handshakeService.getHandshakeByName(handshakeName);
        return ResponseEntity.ok(ApiResponse.success("Handshake retrieved", handshake));
    }

    /**
     * Retrieves a handshake where the given user is the acceptor.
     *
     * @param username the acceptor's username
     * @return response containing handshake data
     */
    @GetMapping("/get-handshake-by-acceptor/{username}")
    public ResponseEntity<ApiResponse<HandshakeDto>> getHandshakeByAcceptor(
            @PathVariable String username) {
        HandshakeDto handshake = handshakeService.getHandshakeByAcceptor(username);
        return ResponseEntity.ok(ApiResponse.success("Handshake retrieved", handshake));
    }

    /**
     * Retrieves a handshake where the given user is the initiator.
     *
     * @param username the initiator's username
     * @return response containing handshake data
     */
    @GetMapping("/get-handshake-by-initiator/{username}")
    public ResponseEntity<ApiResponse<HandshakeDto>> getHandshakeByInitiator(
            @PathVariable String username) {
        HandshakeDto handshake = handshakeService.getHandshakeByInitiator(username);
        return ResponseEntity.ok(ApiResponse.success("Handshake retrieved", handshake));
    }

    /**
     * Retrieves all handshakes initiated by a user.
     *
     * @param username the initiator's username
     * @return response containing list of handshakes
     */
    @GetMapping("/get-handshakes-by-initiator/{username}")
    public ResponseEntity<ApiResponse<List<HandshakeDto>>> getHandshakesByInitiator(
            @PathVariable String username) {
        List<HandshakeDto> handshakes = handshakeService.getHandshakesByInitiator(username);
        return ResponseEntity.ok(ApiResponse.success("Handshakes retrieved", handshakes));
    }

    /**
     * Retrieves all handshakes accepted by a user.
     *
     * @param username the acceptor's username
     * @return response containing list of handshakes
     */
    @GetMapping("/get-handshakes-by-acceptor/{username}")
    public ResponseEntity<ApiResponse<List<HandshakeDto>>> getHandshakesByAcceptor(
            @PathVariable String username) {
        List<HandshakeDto> handshakes = handshakeService.getHandshakesByAcceptor(username);
        return ResponseEntity.ok(ApiResponse.success("Handshakes retrieved", handshakes));
    }

    /**
     * Accepts a handshake by updating its status to ACCEPTED.
     *
     * @param handshakeName the name of the handshake to accept
     * @return response indicating success
     */
    @PutMapping("/accept-handshake")
    public ResponseEntity<ApiResponse<Void>> acceptHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.ACCEPTED);
        return ResponseEntity.ok(ApiResponse.success("Handshake accepted"));
    }

    /**
     * Rejects a handshake by updating its status to CANCELLED.
     *
     * @param handshakeName the name of the handshake to reject
     * @return response indicating success
     */
    @PutMapping("/reject-handshake")
    public ResponseEntity<ApiResponse<Void>> rejectHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.CANCELLED);
        return ResponseEntity.ok(ApiResponse.success("Handshake rejected"));
    }

    /**
     * Completes a handshake by updating its status to COMPLETED.
     *
     * @param handshakeName the name of the handshake to complete
     * @return response indicating success
     */
    @PutMapping("/complete-handshake")
    public ResponseEntity<ApiResponse<Void>> completeHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.COMPLETED);
        return ResponseEntity.ok(ApiResponse.success("Handshake completed"));
    }

    /**
     * Cancels a handshake by updating its status to CANCELLED.
     *
     * @param handshakeName the name of the handshake to cancel
     * @return response indicating success
     */
    @PutMapping("/cancel-handshake")
    public ResponseEntity<ApiResponse<Void>> cancelHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.CANCELLED);
        return ResponseEntity.ok(ApiResponse.success("Handshake canceled"));
    }
}
