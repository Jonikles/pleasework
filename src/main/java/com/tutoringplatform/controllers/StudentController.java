// FILE: src/main/java/com/tutoringplatform/controllers/StudentController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.AddFundsRequest;
import com.tutoringplatform.dto.response.StudentResponse;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.services.StudentService;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import com.tutoringplatform.services.FileService;
import com.tutoringplatform.dto.request.UpdateStudentRequest;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private FileService fileService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private DTOMapper dtoMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable String id) {
        try {
            Student student = studentService.findById(id);
            return ResponseEntity.ok(dtoMapper.toStudentResponse(student));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody UpdateStudentRequest request) {
    try {
        Student updatedStudent = studentService.updateStudent(id, request);
        return ResponseEntity.ok(dtoMapper.toStudentResponse(updatedStudent));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    // Add endpoint to update profile picture
    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileId = fileService.storeFile(id, file, "profile");

            Student student = studentService.findById(id);

            // Delete old profile picture if exists
            if (student.getProfilePictureId() != null) {
                try {
                    fileService.deleteFile(student.getProfilePictureId());
                } catch (Exception e) {
                    // Log error but continue
                }
            }

            student.setProfilePictureId(fileId);
            studentService.update(student);

            return ResponseEntity.ok(Map.of(
                    "profilePictureId", fileId,
                    "profilePictureUrl", "/api/files/" + fileId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/add-funds")
    public ResponseEntity<?> addFunds(@PathVariable String id, @RequestBody AddFundsRequest request) {
        try {
            double balance = studentService.addFunds(id, request.getAmount());
            return ResponseEntity.ok(dtoMapper.toBalanceResponse(balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String id) {
        try {
            double balance = studentService.getBalance(id);
            return ResponseEntity.ok(dtoMapper.toBalanceResponse(balance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam String name) {
        List<Student> students = studentService.searchByName(name);
        List<StudentResponse> responses = students.stream()
                .map(dtoMapper::toStudentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}