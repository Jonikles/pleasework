// FILE: src/main/java/com/tutoringplatform/controllers/SubjectController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.SubjectRequest;
import com.tutoringplatform.dto.response.SubjectResponse;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.services.SubjectService;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private DTOMapper dtoMapper;

    @GetMapping
    public ResponseEntity<?> getAllSubjects() {
        List<Subject> subjects = subjectService.findAll();
        List<SubjectResponse> responses = subjects.stream()
                .map(dtoMapper::toSubjectResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable String id) {
        try {
            Subject subject = subjectService.findById(id);
            return ResponseEntity.ok(dtoMapper.toSubjectResponse(subject));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getSubjectByName(@PathVariable String name) {
        try {
            Subject subject = subjectService.findByName(name);
            return ResponseEntity.ok(dtoMapper.toSubjectResponse(subject));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getSubjectsByCategory(@PathVariable String category) {
        List<Subject> subjects = subjectService.findByCategory(category);
        List<SubjectResponse> responses = subjects.stream()
            .map(dtoMapper::toSubjectResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody SubjectRequest request) {
        try {
            Subject subject = subjectService.createSubject(request.getName(), request.getCategory());
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toSubjectResponse(subject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}