package com.tutoringplatform.user.tutor;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.file.FileService;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.review.ReviewService;
import com.tutoringplatform.user.UserService;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.shared.dto.request.UpdateProfileRequest;
import com.tutoringplatform.shared.dto.response.SubjectResponse;
import com.tutoringplatform.shared.dto.response.TutorProfileResponse;
import com.tutoringplatform.shared.dto.response.ValueResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.user.availability.model.RecurringAvailability;
import com.tutoringplatform.user.availability.model.TutorAvailability;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.authentication.exceptions.EmailAlreadyExistsException;
import com.tutoringplatform.authentication.exceptions.InvalidTimezoneException;
import com.tutoringplatform.user.exceptions.InvalidPasswordException;
import com.tutoringplatform.user.tutor.exceptions.TutorTeachesSubjectException;
import com.tutoringplatform.subject.exceptions.SubjectNotFoundException;
import com.tutoringplatform.user.tutor.exceptions.TutorNotTeachingSubjectException;
import com.tutoringplatform.user.tutor.exceptions.TutorHasBookingsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorService extends UserService<Tutor> {

    private final Logger logger = LoggerFactory.getLogger(TutorService.class);
    private final ITutorRepository repository;
    private final SubjectService subjectService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final AvailabilityService availabilityService;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;
    @Autowired
    public TutorService(
            ITutorRepository repository,
            SubjectService subjectService,
            BookingService bookingService,
            ReviewService reviewService,
            AvailabilityService availabilityService,
            FileService fileService,
            PasswordEncoder passwordEncoder,
            DTOMapper dtoMapper) {
        super(repository);
        this.repository = repository;
        this.subjectService = subjectService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
        this.availabilityService = availabilityService;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.dtoMapper = dtoMapper;
    }

    public TutorProfileResponse getTutorProfile(String tutorId) throws UserNotFoundException {
        Tutor tutor = findById(tutorId);

        // Get reviews
        List<Review> allReviews = reviewService.getTutorReviews(tutorId);

        // Calculate average rating
        double averageRating = allReviews.isEmpty() ? 0.0
                : allReviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0);

        // Count completed sessions
        List<Booking> bookings = bookingService.getTutorBookingList(tutorId);
        int completedSessions = (int) bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count();

        // Map subjects
        List<SubjectResponse> subjects = tutor.getSubjects().stream()
                .map(dtoMapper::toSubjectResponse)
                .collect(Collectors.toList());

        // Get availability
        TutorAvailability availability = availabilityService.getTutorAvailability(tutorId);
        List<RecurringAvailability> recurringSlots = availability != null ? availability.getRecurringSlots()
                : new ArrayList<>();

        
        // Calculate joined date (would normally come from audit fields)
        LocalDate joinedDate = LocalDate.now().minusYears(1); // Placeholder

        return dtoMapper.toTutorProfileResponse(
                tutor,
                averageRating,
                allReviews.size(),
                subjects,
                recurringSlots,
                completedSessions,
                joinedDate);
    }

    public List<TutorProfileResponse> getAllTutorProfiles() throws UserNotFoundException {
        List<Tutor> tutors = findAll();
        List<TutorProfileResponse> profiles = new ArrayList<>();

        for (Tutor tutor : tutors) {
            profiles.add(getTutorProfile(tutor.getId()));
        }

        return profiles;
    }

    @Transactional
    public TutorProfileResponse updateTutorProfile(String tutorId, UpdateProfileRequest request) throws UserNotFoundException, EmailAlreadyExistsException, InvalidPasswordException, InvalidTimezoneException {
        Tutor tutor = findById(tutorId);

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            tutor.setName(request.getName());
        }

        // Update email if provided and not taken
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Tutor existing = repository.findByEmail(request.getEmail());
            if (existing != null && !existing.getId().equals(tutorId)) {
                throw new EmailAlreadyExistsException(request.getEmail());
            }
            tutor.setEmail(request.getEmail());
        }

        // Update password if provided with current password verification
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new InvalidPasswordException("Current password is required");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), tutor.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }

            tutor.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update timezone if provided
        if (request.getTimeZoneId() != null) {
            try {
                ZoneId zone = ZoneId.of(request.getTimeZoneId());
                tutor.setTimeZone(zone);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid timezone: {}", request.getTimeZoneId());
                throw new InvalidTimezoneException(request.getTimeZoneId());
            }
        }

        // Update tutor-specific fields
        if (request.getHourlyRate() > 0) {
            tutor.setHourlyRate(request.getHourlyRate());
        }

        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            tutor.setDescription(request.getDescription());
        }

        repository.update(tutor);

        return getTutorProfile(tutorId);
    }

    @Transactional
    public Map<String, String> updateProfilePicture(String tutorId, MultipartFile file) throws UserNotFoundException {
        Tutor tutor = findById(tutorId);

        // Delete old profile picture if exists
        if (tutor.getProfilePictureId() != null) {
            try {
                fileService.deleteFile(tutor.getProfilePictureId());
            } catch (Exception e) {
                logger.warn("Couldn't delete old profile picture: {}", e.getMessage());
            }
        }

        // Store new profile picture
        String fileId = fileService.storeFile(tutorId, file, "profile");
        tutor.setProfilePictureId(fileId);
        repository.update(tutor);

        Map<String, String> result = new HashMap<>();
        result.put("profilePictureId", fileId);
        result.put("profilePictureUrl", "/api/files/" + fileId);

        return result;
    }

    @Transactional
    public TutorProfileResponse addSubjectToTutor(String tutorId, String subjectId) throws UserNotFoundException, TutorTeachesSubjectException, SubjectNotFoundException {
        Tutor tutor = findById(tutorId);
        Subject subject = subjectService.findById(subjectId);

        if (subject == null) {
            logger.error("Subject not found: {}", subjectId);
            throw new SubjectNotFoundException(subjectId);
        }

        if (tutor.getSubjects().contains(subject)) {
            logger.error("Tutor {} already teaches this subject: {}", tutorId, subjectId);
            throw new TutorTeachesSubjectException(tutorId, subjectId);
        }

        tutor.addSubject(subject);
        repository.update(tutor);

        return getTutorProfile(tutorId);
    }

    @Transactional
    public TutorProfileResponse removeSubjectFromTutor(String tutorId, String subjectId)
        throws UserNotFoundException, SubjectNotFoundException, TutorNotTeachingSubjectException, TutorHasBookingsException {
        Tutor tutor = findById(tutorId);
        Subject subject = subjectService.findById(subjectId);

        if (!tutor.getSubjects().contains(subject)) {
            logger.error("Tutor {} does not teach this subject: {}", tutorId, subjectId);
            throw new TutorNotTeachingSubjectException(tutorId, subjectId);
        }

        // Check if there are any bookings for this subject
        //TODO: make bookingservicem ethod to return bookings by subject
        List<Booking> bookingsForSubject = bookingService.getTutorBookingsBySubject(tutorId, subjectId);
        if (!bookingsForSubject.isEmpty()) {
            logger.error("Cannot remove subject with existing bookings: {}", subjectId);
            throw new TutorHasBookingsException(tutorId, subjectId);
        }

        tutor.removeSubject(subject);
        repository.update(tutor);

        return getTutorProfile(tutorId);
    }

    public ValueResponse<Double> getEarnings(String tutorId) throws UserNotFoundException {
        Tutor tutor = findById(tutorId);
        return dtoMapper.toValueResponse(tutor.getEarnings());
    }

    public ValueResponse<Double> getAverageRating(String tutorId) {
        List<Review> reviews = reviewService.getTutorReviews(tutorId);

        double averageRating = reviews.isEmpty() ? 0.0
                : reviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0);

        return dtoMapper.toValueResponse(averageRating);
    }

    public List<Tutor> findBySubject(Subject subject) {
        return repository.findBySubject(subject);
    }
}