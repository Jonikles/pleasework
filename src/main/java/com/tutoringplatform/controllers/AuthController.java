package com.tutoringplatform.controllers;

import com.tutoringplatform.models.*;
import com.tutoringplatform.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.email, request.password);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/signup/student")
    public ResponseEntity<?> signupStudent(@RequestBody StudentSignupRequest request) {
        try {
            Student student = authService.signupStudent(request.name, request.email, request.password);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/signup/tutor")
    public ResponseEntity<?> signupTutor(@RequestBody TutorSignupRequest request) {
        try {
            Tutor tutor = authService.signupTutor(request.name, request.email, request.password, request.hourlyRate, request.description);
            return ResponseEntity.ok(tutor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    static class LoginRequest {
        public String email;
        public String password;
    }

    static class StudentSignupRequest {
        public String name;
        public String email;
        public String password;
    }

    static class TutorSignupRequest {
        public String name;
        public String email;
        public String password;
        public double hourlyRate;
        public String description;
    }
}