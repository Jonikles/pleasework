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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

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
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student student) {
        try {
            studentService.update(student);
            return ResponseEntity.ok(dtoMapper.toStudentResponse(student));
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