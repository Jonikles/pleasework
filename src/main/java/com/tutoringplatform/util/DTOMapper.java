// FILE: src/main/java/com/tutoringplatform/util/DTOMapper.java
package com.tutoringplatform.util;

import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.models.*;
import com.tutoringplatform.models.availability.TutorAvailability;
import com.tutoringplatform.services.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class DTOMapper {
    @Autowired
    private AvailabilityService availabilityService;

    public StudentResponse toStudentResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setBalance(student.getBalance());
        return response;
    }

    public TutorResponse toTutorResponse(Tutor tutor) {
        TutorResponse response = new TutorResponse();
        response.setId(tutor.getId());
        response.setName(tutor.getName());
        response.setEmail(tutor.getEmail());
        response.setHourlyRate(tutor.getHourlyRate());
        response.setDescription(tutor.getDescription());
        response.setAverageRating(tutor.getAverageRating());
        response.setEarnings(tutor.getEarnings());
        response.setTimeZoneId(tutor.getTimeZoneId());

        if (tutor.getSubjects() != null) {
            response.setSubjects(tutor.getSubjects().stream()
                    .map(this::toSubjectResponse)
                    .collect(Collectors.toList()));
        }

        try {
            TutorAvailability availability = availabilityService.getAvailability(tutor.getId());
            response.setAvailability(availability.getRecurringSlots());
        } catch (Exception e) {
            response.setAvailability(new ArrayList<>());
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
        return response;
    }

    public ReviewResponse toReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setStudentId(review.getStudentId());
        response.setTutorId(review.getTutorId());
        response.setBookingId(review.getBookingId());
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
        if (user instanceof Student) {
            return toStudentResponse((Student) user);
        } else if (user instanceof Tutor) {
            return toTutorResponse((Tutor) user);
        }
        throw new IllegalArgumentException("Unknown user type");
    }
}