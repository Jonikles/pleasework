// FILE: src/main/java/com/tutoringplatform/util/DTOMapper.java
package com.tutoringplatform.util;

import java.time.ZoneId;
import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.models.*;
import com.tutoringplatform.models.availability.TutorAvailability;
import com.tutoringplatform.services.AvailabilityService;
import com.tutoringplatform.services.StudentService;
import com.tutoringplatform.services.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.tutoringplatform.config.AppProperties;

@Component
public class DTOMapper {
    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TutorService tutorService;
    @Autowired
    private AppProperties appProperties;

    public StudentResponse toStudentResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setBalance(student.getBalance());
        response.setTimeZoneId(student.getTimeZoneId());
        if (student.getProfilePictureId() != null) {
            response.setProfilePictureUrl(appProperties.getApi().getFilesBaseUrl() + student.getProfilePictureId());
        }
        return response;
    }

    public TutorResponse toTutorResponse(Tutor tutor) {
        TutorResponse response = new TutorResponse();
        response.setId(tutor.getId());
        response.setName(tutor.getName());
        response.setEmail(tutor.getEmail());
        response.setHourlyRate(tutor.getHourlyRate());
        response.setDescription(tutor.getDescription());
        response.setEarnings(tutor.getEarnings());
        response.setTimeZoneId(tutor.getTimeZoneId());

        if (tutor.getSubjects() != null) {
            response.setSubjects(tutor.getSubjects().stream()
                    .map(this::toSubjectResponse)
                    .collect(Collectors.toList()));
        }

        if (tutor.getReviewsReceived() != null) {
            response.setAverageRating(tutor.getReviewsReceived().stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0));
        }

        try {
            TutorAvailability availability = availabilityService.getAvailability(tutor.getId());
            response.setAvailability(availability.getRecurringSlots());
        } catch (Exception e) {
            response.setAvailability(new ArrayList<>());
        }

        if (tutor.getProfilePictureId() != null) {
            response.setProfilePictureUrl(appProperties.getApi().getFilesBaseUrl() + tutor.getProfilePictureId());
        }

        return response;
    }

    public SubjectResponse toSubjectResponse(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setCategory(subject.getCategory());
        return response;
    }

    public BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setStudentId(booking.getStudentId());
        response.setTutorId(booking.getTutorId());
        response.setSubject(toSubjectResponse(booking.getSubject()));
        response.setDateTime(booking.getDateTime());
        response.setDurationHours(booking.getDurationHours());
        response.setTotalCost(booking.getTotalCost());
        response.setStatus(booking.getStatus().toString());
    
    // Add timezone info
        try {
            Student student = studentService.findById(booking.getStudentId());
            response.setStudentTimeZoneId(student.getTimeZoneId());
        } catch (Exception e) {
            // Use default if not found
            response.setStudentTimeZoneId(ZoneId.systemDefault().getId());
        }
        
        try {
            Tutor tutor = tutorService.findById(booking.getTutorId());
            response.setTutorTimeZoneId(tutor.getTimeZoneId());
        } catch (Exception e) {
            // Use default if not found
            response.setTutorTimeZoneId(ZoneId.systemDefault().getId());
        }
        
        return response;
    }
    public ReviewResponse toReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setStudentId(review.getStudentId());
        response.setTutorId(review.getTutorId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setTimestamp(review.getTimestamp());
        return response;
    }

    public AverageRatingResponse toAverageRatingResponse(double averageRating) {
        AverageRatingResponse response = new AverageRatingResponse();
        response.setAverageRating(averageRating);
        return response;
    }

    public EarningsResponse toEarningsResponse(double earnings) {
        EarningsResponse response = new EarningsResponse();
        response.setEarnings(earnings);
        return response;
    }

    public BalanceResponse toBalanceResponse(double balance) {
        BalanceResponse response = new BalanceResponse();
        response.setBalance(balance);
        return response;
    }

    public Object toUserResponse(User user) {
        if (user.getUserType() == UserType.STUDENT) {
            return toStudentResponse((Student) user);
        } else if (user.getUserType() == UserType.TUTOR) {
            return toTutorResponse((Tutor) user);
        }
        throw new IllegalArgumentException("Unknown user type: " + user.getUserType());
    }
}