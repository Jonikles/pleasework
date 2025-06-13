// FILE: src/main/java/com/tutoringplatform/controllers/TutorController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.AvailabilityRequest;
import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.models.*;
import com.tutoringplatform.services.TutorService;
import com.tutoringplatform.services.SubjectService;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tutors")
@CrossOrigin(origins = "*")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private DTOMapper dtoMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTutor(@PathVariable String id) {
        try {
            Tutor tutor = tutorService.findById(id);
            return ResponseEntity.ok(dtoMapper.toTutorResponse(tutor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTutors() {
        List<Tutor> tutors = tutorService.findAll();
        List<TutorResponse> responses = tutors.stream()
                .map(dtoMapper::toTutorResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTutor(@PathVariable String id, @RequestBody Tutor tutor) {
        try {
            if (!id.equals(tutor.getId())) {
                return ResponseEntity.badRequest().body("ID mismatch");
            }
            tutorService.update(tutor);
            return ResponseEntity.ok(dtoMapper.toTutorResponse(tutor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTutors(
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) Integer hour) {
        try {
            Subject subject = null;
            if (subjectId != null) {
                subject = subjectService.findById(subjectId);
            }
            List<Tutor> tutors = tutorService.searchTutors(subject, minPrice, maxPrice, minRating, day, hour);
            List<TutorResponse> responses = tutors.stream()
                    .map(dtoMapper::toTutorResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(
            @PathVariable String id,
            @RequestBody AvailabilityRequest request) {
        try {
            tutorService.updateAvailability(id, request.getDay(), request.getHour(), request.isAdd());
            Tutor tutor = tutorService.findById(id);
            return ResponseEntity.ok(tutor.getAvailability());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<?> addSubject(@PathVariable String id, @PathVariable String subjectId) {
        try {
            Subject subject = subjectService.findById(subjectId);
            Tutor tutor = tutorService.findById(id);
            tutor.addSubject(subject);
            tutorService.update(tutor);
            List<SubjectResponse> subjectResponses = tutor.getSubjects().stream()
                    .map(dtoMapper::toSubjectResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(subjectResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<?> removeSubject(@PathVariable String id, @PathVariable String subjectId) {
        try {
            Subject subject = subjectService.findById(subjectId);
            Tutor tutor = tutorService.findById(id);
            tutor.removeSubject(subject);
            tutorService.update(tutor);
            List<SubjectResponse> subjectResponses = tutor.getSubjects().stream()
                    .map(dtoMapper::toSubjectResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(subjectResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/earnings")
    public ResponseEntity<?> getEarnings(@PathVariable String id) {
        try {
            Tutor tutor = tutorService.findById(id);
            return ResponseEntity.ok(new EarningsResponse(tutor.getEarnings()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}