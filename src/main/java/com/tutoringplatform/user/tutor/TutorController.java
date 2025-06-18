package com.tutoringplatform.user.tutor;

import com.tutoringplatform.shared.dto.request.UpdateProfileRequest;
import com.tutoringplatform.shared.dto.response.TutorProfileResponse;
import com.tutoringplatform.shared.dto.response.ValueResponse;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.shared.dto.request.AddSubjectToTutorRequest;
import com.tutoringplatform.shared.dto.response.AvailabilityResponse;
import com.tutoringplatform.shared.dto.request.TutorAvailabilityRequest;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.subject.exceptions.SubjectNotFoundException;
import com.tutoringplatform.user.tutor.exceptions.TutorNotTeachingSubjectException;
import com.tutoringplatform.user.tutor.exceptions.TutorHasBookingsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorService tutorService;
    private final AvailabilityService availabilityService;

    @Autowired
    public TutorController(TutorService tutorService, AvailabilityService availabilityService) {
        this.tutorService = tutorService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTutor(@PathVariable String id) {
        try {
            TutorProfileResponse profile = tutorService.getTutorProfile(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTutors() {
        try {
            List<TutorProfileResponse> tutors = tutorService.getAllTutorProfiles();
            return ResponseEntity.ok(tutors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTutor(@PathVariable String id, @RequestBody UpdateProfileRequest request) {
        try {
            TutorProfileResponse profile = tutorService.updateTutorProfile(id, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, String> result = tutorService.updateProfilePicture(id, file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(
            @PathVariable String id,
            @RequestBody TutorAvailabilityRequest request) {
        try {
            AvailabilityResponse availability = availabilityService.updateTutorAvailability(id, request);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<?> getTutorAvailability(@PathVariable String id) {
        try {
            AvailabilityResponse availability = availabilityService.getTutorAvailability(id);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<?> addSubject(@PathVariable String id, @RequestBody AddSubjectToTutorRequest request) {
        try {
            TutorProfileResponse profile = tutorService.addSubjectToTutor(id, request.getSubjectId());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<?> removeSubject(@PathVariable String id, @PathVariable String subjectId)
        throws UserNotFoundException, SubjectNotFoundException, TutorNotTeachingSubjectException, TutorHasBookingsException {
        try {
            TutorProfileResponse profile = tutorService.removeSubjectFromTutor(id, subjectId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/earnings")
    public ResponseEntity<?> getEarnings(@PathVariable String id) {
        try {
            ValueResponse<Double> earnings = tutorService.getEarnings(id);
            return ResponseEntity.ok(earnings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable String id) {
        try {
            ValueResponse<Double> rating = tutorService.getAverageRating(id);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}