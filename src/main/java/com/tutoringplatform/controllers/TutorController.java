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
import java.util.Map;
import com.tutoringplatform.services.FileService;
import com.tutoringplatform.dto.request.UpdateTutorRequest;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorService tutorService;
    private final AvailabilityService availabilityService;
    private final FileService fileService;
    private final DTOMapper dtoMapper;

    @Autowired
    public TutorController(TutorService tutorService, AvailabilityService availabilityService, FileService fileService,
            DTOMapper dtoMapper) {
        this.tutorService = tutorService;
        this.availabilityService = availabilityService;
        this.fileService = fileService;
        this.dtoMapper = dtoMapper;
    }

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
    public ResponseEntity<?> updateTutor(@PathVariable String id, @RequestBody UpdateTutorRequest request) {
        try {
            Tutor updatedTutor = tutorService.updateTutor(id, request);
            return ResponseEntity.ok(dtoMapper.toTutorResponse(updatedTutor));
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

            Tutor tutor = tutorService.findById(id);

            // Delete old profile picture if exists
            if (tutor.getProfilePictureId() != null) {
                try {
                    fileService.deleteFile(tutor.getProfilePictureId());
                } catch (Exception e) {
                    // Log error but continue
                }
            }

            tutor.setProfilePictureId(fileId);
            tutorService.update(tutor);

            return ResponseEntity.ok(Map.of(
                    "profilePictureId", fileId,
                    "profilePictureUrl", "/api/files/" + fileId));
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