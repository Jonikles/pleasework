package com.tutoringplatform.controllers;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<?> getAllSubjects() {
        List<Subject> subjects = subjectService.findAll();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable String id) {
        try {
            Subject subject = subjectService.findById(id);
            return ResponseEntity.ok(subject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getSubjectByName(@PathVariable String name) {
        try {
            Subject subject = subjectService.findByName(name);
            return ResponseEntity.ok(subject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getSubjectsByCategory(@PathVariable String category) {
        List<Subject> subjects = subjectService.findByCategory(category);
        return ResponseEntity.ok(subjects);
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody SubjectRequest request) {
        try {
            Subject subject = subjectService.createSubject(request.name, request.category);
            return ResponseEntity.status(HttpStatus.CREATED).body(subject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    static class SubjectRequest {
        public String name;
        public String category;
    }
}