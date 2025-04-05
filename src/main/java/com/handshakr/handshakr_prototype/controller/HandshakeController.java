package com.handshakr.handshakr_prototype.controller;

import com.handshakr.handshakr_prototype.handshake.HandshakeService;
import com.handshakr.handshakr_prototype.handshake.HandshakeStatus;
import com.handshakr.handshakr_prototype.handshake.dto.*;
import com.handshakr.handshakr_prototype.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/handshake")
public class HandshakeController {
    private final HandshakeService handshakeService;

    public HandshakeController(HandshakeService handshakeService) {
        this.handshakeService = handshakeService;
    }

    @GetMapping("/get-handshake-by-name/{handshakeName}")
    public ResponseEntity<ApiResponse<HandshakeDto>> getHandshakeByName(
            @PathVariable String handshakeName) {
        HandshakeDto handshake = handshakeService.getHandshakeByName(handshakeName);
        return ResponseEntity.ok(ApiResponse.success("Handshake retrieved", handshake));
    }

    @GetMapping("/get-handshake-by-acceptor/{username}")
    public ResponseEntity<ApiResponse<HandshakeDto>> getHandshakeByAcceptor(
            @PathVariable String username) {
        HandshakeDto handshake = handshakeService.getHandshakeByAcceptor(username);
        return ResponseEntity.ok(ApiResponse.success("Handshake retrieved", handshake));
    }

    @GetMapping("/get-handshake-by-initiator/{username}")
    public ResponseEntity<ApiResponse<HandshakeDto>> getHandshakeByInitiator(
            @PathVariable String username) {
        HandshakeDto handshake = handshakeService.getHandshakeByInitiator(username);
        return ResponseEntity.ok(ApiResponse.success("Handshake retrieved", handshake));
    }

    @GetMapping("/get-handshakes-by-initiator/{username}")
    public ResponseEntity<ApiResponse<List<HandshakeDto>>> getHandshakesByInitiator(
            @PathVariable String username) {
        List<HandshakeDto> handshakes = handshakeService.getHandshakesByInitiator(username);
        return ResponseEntity.ok(ApiResponse.success("Handshakes retrieved", handshakes));
    }

    @GetMapping("/get-handshakes-by-acceptor/{username}")
    public ResponseEntity<ApiResponse<List<HandshakeDto>>> getHandshakesByAcceptor(
            @PathVariable String username) {
        List<HandshakeDto> handshakes = handshakeService.getHandshakesByAcceptor(username);
        return ResponseEntity.ok(ApiResponse.success("Handshakes retrieved", handshakes));
    }

    @PutMapping("/accept-handshake")
    public ResponseEntity<ApiResponse<Void>> acceptHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.ACCEPTED);
        return ResponseEntity.ok(ApiResponse.success("Handshake accepted"));
    }

    @PutMapping("/reject-handshake")
    public ResponseEntity<ApiResponse<Void>> rejectHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.CANCELLED);
        return ResponseEntity.ok(ApiResponse.success("Handshake rejected"));
    }

    @PutMapping("/complete-handshake")
    public ResponseEntity<ApiResponse<Void>> completeHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.COMPLETED);
        return ResponseEntity.ok(ApiResponse.success("Handshake completed"));
    }

    @PutMapping("/cancel-handshake")
    public ResponseEntity<ApiResponse<Void>> cancelHandshake(@RequestParam String handshakeName) {
        handshakeService.updateHandshake(handshakeName, HandshakeStatus.CANCELLED);
        return ResponseEntity.ok(ApiResponse.success("Handshake canceled"));
    }
}
