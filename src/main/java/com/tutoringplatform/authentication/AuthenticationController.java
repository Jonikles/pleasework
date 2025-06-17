package com.tutoringplatform.authentication;

import com.tutoringplatform.shared.dto.request.LoginRequest;
import com.tutoringplatform.shared.dto.request.SignupRequest;
import com.tutoringplatform.shared.dto.response.AuthResponse;
import com.tutoringplatform.authentication.authExceptions.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) throws AuthenticationException {
        logger.debug("Login request received for email: {}", request.getEmail());
        AuthResponse response = authenticationService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest request) throws AuthenticationException {
        logger.debug("Signup request received for type {} email: {}", request.getUserType(), request.getEmail());
        AuthResponse response = authenticationService.signup(request);
        return ResponseEntity.ok(response);
    }
}