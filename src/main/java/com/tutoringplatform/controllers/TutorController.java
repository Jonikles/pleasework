// FILE: src/main/java/com/tutoringplatform/controllers/TutorController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.AvailabilityRequest;
import com.tutoringplatform.dto.response.TutorResponse;
import com.tutoringplatform.dto.response.SubjectResponse;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.availability.TutorAvailability;
import com.tutoringplatform.services.AvailabilityService;
import com.tutoringplatform.services.TutorService;
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
    private AvailabilityService availabilityService;

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
            tutorService.update(tutor);
            return ResponseEntity.ok(dtoMapper.toTutorResponse(tutor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{id}/availability")
    public ResponseEntity<?> addAvailability(
            @PathVariable String id,
            @RequestBody AvailabilityRequest request) {
        try {
            availabilityService.addRecurringAvailability(
                    id,
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime());

            TutorAvailability availability = availabilityService.getAvailability(id);
            return ResponseEntity.ok(availability.getRecurringSlots());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/availability")
    public ResponseEntity<?> removeAvailability(
            @PathVariable String id,
            @RequestBody AvailabilityRequest request) {
        try {
            availabilityService.removeRecurringAvailability(
                    id,
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime());

            TutorAvailability availability = availabilityService.getAvailability(id);
            return ResponseEntity.ok(availability.getRecurringSlots());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(
            @PathVariable String id,
            @RequestBody AvailabilityRequest request) {
        try {
            availabilityService.addRecurringAvailability(
                    id,
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime());

            TutorAvailability availability = availabilityService.getAvailability(id);
            return ResponseEntity.ok(availability.getRecurringSlots());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

    @GetMapping("/{id}/availability")
    public ResponseEntity<?> getTutorAvailability(@PathVariable String id) {
        try {
            TutorAvailability availability = availabilityService.getAvailability(id);
            // Convert to a response DTO
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<?> addSubject(@PathVariable String id, @PathVariable String subjectId) {
        try {
            List<Subject> subjects = tutorService.addSubjectToTutor(id, subjectId);
            List<SubjectResponse> responses = subjects.stream()
                    .map(dtoMapper::toSubjectResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<?> removeSubject(@PathVariable String id, @PathVariable String subjectId) {
        try {
            List<Subject> subjects = tutorService.removeSubjectFromTutor(id, subjectId);
            List<SubjectResponse> responses = subjects.stream()
                    .map(dtoMapper::toSubjectResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/earnings")
    public ResponseEntity<?> getEarnings(@PathVariable String id) {
        try {
            double earnings = tutorService.getEarnings(id);
            return ResponseEntity.ok(dtoMapper.toEarningsResponse(earnings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable String id) {
        try {
            double averageRating = tutorService.getAverageRating(id);
            return ResponseEntity.ok(dtoMapper.toAverageRatingResponse(averageRating));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}