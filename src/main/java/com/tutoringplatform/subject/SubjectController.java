package com.tutoringplatform.subject;

import com.tutoringplatform.shared.dto.response.SubjectListResponse;
import com.tutoringplatform.shared.dto.response.SubjectResponse;
import com.tutoringplatform.shared.dto.request.CreateSubjectRequest;
import com.tutoringplatform.subject.exceptions.*;
import com.tutoringplatform.user.tutor.exceptions.TutorNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final Logger logger = LoggerFactory.getLogger(SubjectController.class);
    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody CreateSubjectRequest request) throws SubjectExistsException {
        logger.debug("Creating subject: {}", request.getName());
        SubjectResponse subject = subjectService.createSubject(request);
        return ResponseEntity.ok(subject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable String id) throws AssignedSubjectException, SubjectNotFoundException {
        logger.debug("Deleting subject: {}", id);
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Subject deleted successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllSubjects() {
        logger.debug("Getting all subjects");
        SubjectListResponse subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/category")
    public ResponseEntity<?> getAllSubjectsByCategory() {
        logger.debug("Getting all subjects by category");
        SubjectListResponse subjects = subjectService.getAllSubjectsByCategory();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable String id) throws SubjectNotFoundException {
        logger.debug("Getting subject by id: {}", id);
        SubjectResponse subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @GetMapping("/available/tutor/{tutorId}")
    public ResponseEntity<?> getAvailableSubjectsForTutor(@PathVariable String tutorId) throws TutorNotFoundException {
        logger.debug("Getting available subjects for tutor: {}", tutorId);
        List<SubjectResponse> subjects = subjectService.getAvailableSubjectsForTutor(tutorId);
        return ResponseEntity.ok(subjects);
    }
}
