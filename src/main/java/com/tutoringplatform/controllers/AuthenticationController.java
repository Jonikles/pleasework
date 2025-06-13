// FILE: src/main/java/com/tutoringplatform/controllers/AuthController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.LoginRequest;
import com.tutoringplatform.dto.request.StudentSignupRequest;
import com.tutoringplatform.dto.request.TutorSignupRequest;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.User;
import com.tutoringplatform.services.AuthenticationService;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DTOMapper dtoMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authenticationService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(dtoMapper.toUserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/signup/student")
    public ResponseEntity<?> signupStudent(@RequestBody StudentSignupRequest request) {
        try {
            Student student = authenticationService.signupStudent(request.getName(), request.getEmail(), request.getPassword());
            return ResponseEntity.ok(dtoMapper.toStudentResponse(student));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/signup/tutor")
    public ResponseEntity<?> signupTutor(@RequestBody TutorSignupRequest request) {
        try {
            Tutor tutor = authenticationService.signupTutor(request.getName(), request.getEmail(), request.getPassword(),
                    request.getHourlyRate(), request.getDescription());
            return ResponseEntity.ok(dtoMapper.toTutorResponse(tutor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}