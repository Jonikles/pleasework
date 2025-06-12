package com.tutoringplatform.controllers;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable String id) {
        try {
            Student student = studentService.findById(id);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student student) {
        try {
            if (!id.equals(student.getId())) {
                return ResponseEntity.badRequest().body("ID mismatch");
            }
            studentService.update(student);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/add-funds")
    public ResponseEntity<?> addFunds(@PathVariable String id, @RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            studentService.addFunds(id, amount);
            Student student = studentService.findById(id);
            return ResponseEntity.ok(Map.of("balance", student.getBalance()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String id) {
        try {
            Student student = studentService.findById(id);
            return ResponseEntity.ok(Map.of("balance", student.getBalance()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam String name) {
        List<Student> students = studentService.searchByName(name);
        return ResponseEntity.ok(students);
    }
}