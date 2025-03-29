package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.handshake.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/handshake")
public class HandshakeController {
    private final HandshakeService handshakeService;

    public HandshakeController(HandshakeService handshakeService) {
        this.handshakeService = handshakeService;
    }

    @PostMapping("/create-handshake")
    public ResponseEntity<HttpStatus> createHandshake(@RequestBody CreateHandshakeRequest request) {
        handshakeService.createHandshake(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-handshake-by-name")
    public ResponseEntity<HandshakeDto> getHandshake(@RequestBody GetHandshakeByName request) {
        HandshakeDto handshake = handshakeService.getHandshakeByName(request.handshakeName());
        return new ResponseEntity<>(handshake, HttpStatus.OK);
    }

    @GetMapping("/get-handshake-by-acceptor")
    public ResponseEntity<HandshakeDto> getHandshakeByAcceptor(@RequestBody GetHandshakeByUser request) {
        HandshakeDto handshake = handshakeService.getHandshakeByAcceptor(request.userName());
        return new ResponseEntity<>(handshake, HttpStatus.OK);
    }

    @GetMapping("/get-handshake-by-initiator")
    public ResponseEntity<HandshakeDto> getHandshakeByInitiator(@RequestBody GetHandshakeByUser request) {
        HandshakeDto handshake = handshakeService.getHandshakeByAcceptor(request.userName());
        return new ResponseEntity<>(handshake, HttpStatus.OK);
    }

    @PutMapping("/update-handshake")
    public ResponseEntity<HttpStatus> updateHandshake(@RequestBody UpdateHandshakeRequest request) {
        handshakeService.updateHandshake(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
