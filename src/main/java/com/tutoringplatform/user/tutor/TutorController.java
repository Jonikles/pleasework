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
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;
import com.tutoringplatform.authentication.exceptions.EmailAlreadyExistsException;
import com.tutoringplatform.user.exceptions.InvalidPasswordException;
import com.tutoringplatform.authentication.exceptions.InvalidTimezoneException;
import com.tutoringplatform.user.tutor.exceptions.TutorTeachesSubjectException;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;
import java.io.IOException;

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
    public ResponseEntity<?> getTutor(@PathVariable String id) throws UserNotFoundException, NoCompletedBookingsException {
        TutorProfileResponse profile = tutorService.getTutorProfile(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<?> getAllTutors() throws UserNotFoundException, NoCompletedBookingsException {
        List<TutorProfileResponse> tutors = tutorService.getAllTutorProfiles();
        return ResponseEntity.ok(tutors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTutor(@PathVariable String id, @RequestBody UpdateProfileRequest request) throws UserNotFoundException, EmailAlreadyExistsException, InvalidPasswordException, InvalidTimezoneException, NoCompletedBookingsException {
        TutorProfileResponse profile = tutorService.updateTutorProfile(id, request);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) throws UserNotFoundException, IOException {
        Map<String, String> result = tutorService.updateProfilePicture(id, file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(
            @PathVariable String id,
            @RequestBody TutorAvailabilityRequest request) throws UserNotFoundException {
        AvailabilityResponse availability = availabilityService.updateTutorAvailability(id, request);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<?> getTutorAvailability(@PathVariable String id) throws UserNotFoundException {
        AvailabilityResponse availability = availabilityService.getTutorAvailability(id);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<?> addSubject(@PathVariable String id, @RequestBody AddSubjectToTutorRequest request) throws UserNotFoundException, TutorTeachesSubjectException, SubjectNotFoundException, NoCompletedBookingsException {
        TutorProfileResponse profile = tutorService.addSubjectToTutor(id, request.getSubjectId());
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<?> removeSubject(@PathVariable String id, @PathVariable String subjectId) throws UserNotFoundException, SubjectNotFoundException, TutorNotTeachingSubjectException, TutorHasBookingsException, NoCompletedBookingsException, PaymentNotFoundException {
        TutorProfileResponse profile = tutorService.removeSubjectFromTutor(id, subjectId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}/earnings")
    public ResponseEntity<?> getEarnings(@PathVariable String id) throws UserNotFoundException {
        ValueResponse<Double> earnings = tutorService.getEarnings(id);
        return ResponseEntity.ok(earnings);
    }

    @GetMapping("/{id}/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable String id) throws NoCompletedBookingsException, UserNotFoundException {
        ValueResponse<Double> rating = tutorService.getAverageRating(id);
        return ResponseEntity.ok(rating);
    }
}