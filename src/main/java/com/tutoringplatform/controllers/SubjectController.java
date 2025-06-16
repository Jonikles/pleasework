package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.response.SubjectListResponse;
import com.tutoringplatform.dto.response.SubjectResponse;
import com.tutoringplatform.services.SubjectService;
import com.tutoringplatform.dto.request.CreateSubjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody CreateSubjectRequest request) {
        try {
            SubjectResponse subject = subjectService.createSubject(request);
            return ResponseEntity.ok(subject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable String id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.ok("Subject deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSubjects() {
        try {
            SubjectListResponse subjects = subjectService.getAllSubjects();
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> getAllSubjectsByCategory() {
        try {
            SubjectListResponse subjects = subjectService.getAllSubjectsByCategory();
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable String id) {
        try {
            SubjectResponse subject = subjectService.getSubjectById(id);
            return ResponseEntity.ok(subject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/available/tutor/{tutorId}")
    public ResponseEntity<?> getAvailableSubjectsForTutor(@PathVariable String tutorId) {
        try {
            List<SubjectResponse> subjects = subjectService.getAvailableSubjectsForTutor(tutorId);
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
