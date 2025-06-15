package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.AddFundsRequest;
import com.tutoringplatform.dto.request.UpdateProfileRequest;
import com.tutoringplatform.dto.response.StudentProfileResponse;
import com.tutoringplatform.dto.response.ValueResponse;
import com.tutoringplatform.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable String id) {
        try {
            StudentProfileResponse profile = studentService.getStudentProfile(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody UpdateProfileRequest request) {
        try {
            StudentProfileResponse profile = studentService.updateStudentProfile(id, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, String> result = studentService.updateProfilePicture(id, file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/add-funds")
    public ResponseEntity<?> addFunds(@PathVariable String id, @RequestBody AddFundsRequest request) {
        try {
            ValueResponse<Double> balance = studentService.addFunds(id, request.getAmount());
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String id) {
        try {
            ValueResponse<Double> balance = studentService.getBalance(id);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}