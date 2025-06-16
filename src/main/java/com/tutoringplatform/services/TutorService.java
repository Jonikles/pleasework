package com.tutoringplatform.services;

import com.tutoringplatform.dto.request.UpdateProfileRequest;
import com.tutoringplatform.dto.response.TutorProfileResponse;
import com.tutoringplatform.dto.response.ValueResponse;
import com.tutoringplatform.dto.response.SubjectResponse;
import com.tutoringplatform.models.availability.TutorAvailability;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Booking;
import com.tutoringplatform.models.Review;
import com.tutoringplatform.models.availability.RecurringAvailability;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import com.tutoringplatform.repositories.interfaces.ISubjectRepository;
import com.tutoringplatform.repositories.interfaces.IBookingRepository;
import com.tutoringplatform.repositories.interfaces.IReviewRepository;
import com.tutoringplatform.repositories.interfaces.IAvailabilityRepository;
import com.tutoringplatform.util.DTOMapper;
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

    private final ISubjectRepository subjectRepository;
    private final IBookingRepository bookingRepository;
    private final IReviewRepository reviewRepository;
    private final IAvailabilityRepository availabilityRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Autowired
    public TutorService(
            ITutorRepository repository,
            ISubjectRepository subjectRepository,
            IBookingRepository bookingRepository,
            IReviewRepository reviewRepository,
            IAvailabilityRepository availabilityRepository,
            FileService fileService,
            PasswordEncoder passwordEncoder,
            DTOMapper dtoMapper) {
        super(repository);
        this.subjectRepository = subjectRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.availabilityRepository = availabilityRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.dtoMapper = dtoMapper;
    }

    public TutorProfileResponse getTutorProfile(String tutorId) throws Exception {
        Tutor tutor = findById(tutorId);

        // Get reviews
        List<Review> allReviews = reviewRepository.getTutorReviews(tutorId);

        // Calculate average rating
        double averageRating = allReviews.isEmpty() ? 0.0
                : allReviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0);

        // Count completed sessions
        List<Booking> bookings = bookingRepository.findByTutorId(tutorId);
        int completedSessions = (int) bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count();

        // Map subjects
        List<SubjectResponse> subjects = tutor.getSubjects().stream()
                .map(dtoMapper::toSubjectResponse)
                .collect(Collectors.toList());

        // Get availability
        TutorAvailability availability = availabilityRepository.findByTutorId(tutorId);
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

    public List<TutorProfileResponse> getAllTutorProfiles() throws Exception {
        List<Tutor> tutors = findAll();
        List<TutorProfileResponse> profiles = new ArrayList<>();

        for (Tutor tutor : tutors) {
            profiles.add(getTutorProfile(tutor.getId()));
        }

        return profiles;
    }

    @Transactional
    public TutorProfileResponse updateTutorProfile(String tutorId, UpdateProfileRequest request) throws Exception {
        Tutor tutor = findById(tutorId);

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            tutor.setName(request.getName());
        }

        // Update email if provided and not taken
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Tutor existing = repository.findByEmail(request.getEmail());
            if (existing != null && !existing.getId().equals(tutorId)) {
                throw new Exception("Email already in use");
            }
            tutor.setEmail(request.getEmail());
        }

        // Update password if provided with current password verification
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new Exception("Current password required to change password");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), tutor.getPassword())) {
                throw new Exception("Current password is incorrect");
            }

            tutor.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update timezone if provided
        if (request.getTimeZoneId() != null) {
            try {
                ZoneId zone = ZoneId.of(request.getTimeZoneId());
                tutor.setTimeZone(zone);
            } catch (Exception e) {
                throw new Exception("Invalid timezone");
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
    public Map<String, String> updateProfilePicture(String tutorId, MultipartFile file) throws Exception {
        Tutor tutor = findById(tutorId);

        // Delete old profile picture if exists
        if (tutor.getProfilePictureId() != null) {
            try {
                fileService.deleteFile(tutor.getProfilePictureId());
            } catch (Exception e) {
                // Log error but continue
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
    public TutorProfileResponse addSubjectToTutor(String tutorId, String subjectId) throws Exception {
        Tutor tutor = findById(tutorId);
        Subject subject = subjectRepository.findById(subjectId);

        if (subject == null) {
            throw new Exception("Subject not found");
        }

        if (tutor.getSubjects().contains(subject)) {
            throw new Exception("Tutor already teaches this subject");
        }

        tutor.addSubject(subject);
        repository.update(tutor);

        return getTutorProfile(tutorId);
    }

    @Transactional
    public TutorProfileResponse removeSubjectFromTutor(String tutorId, String subjectId) throws Exception {
        Tutor tutor = findById(tutorId);
        Subject subject = subjectRepository.findById(subjectId);

        if (subject == null) {
            throw new Exception("Subject not found");
        }

        if (!tutor.getSubjects().contains(subject)) {
            throw new Exception("Tutor does not teach this subject");
        }

        // Check if there are any bookings for this subject
        List<Booking> bookingsForSubject = bookingRepository.findByTutorIdAndSubjectId(tutorId, subjectId);
        if (!bookingsForSubject.isEmpty()) {
            throw new Exception("Cannot remove subject with existing bookings");
        }

        tutor.removeSubject(subject);
        repository.update(tutor);

        return getTutorProfile(tutorId);
    }

    public ValueResponse<Double> getEarnings(String tutorId) throws Exception {
        Tutor tutor = findById(tutorId);
        return dtoMapper.toValueResponse(tutor.getEarnings());
    }

    public ValueResponse<Double> getAverageRating(String tutorId) throws Exception {
        List<Review> reviews = reviewRepository.getTutorReviews(tutorId);

        double averageRating = reviews.isEmpty() ? 0.0
                : reviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0);

        return dtoMapper.toValueResponse(averageRating);
    }
}