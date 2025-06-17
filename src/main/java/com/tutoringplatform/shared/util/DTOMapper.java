package com.tutoringplatform.shared.util;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.config.AppProperties;
import com.tutoringplatform.notification.Notification;
import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.shared.dto.response.*;
import com.tutoringplatform.shared.dto.response.info.PaymentInfo;
import com.tutoringplatform.shared.dto.response.info.TutorInfo;
import com.tutoringplatform.shared.dto.response.info.TutorSearchResultInfo;
import com.tutoringplatform.shared.dto.response.info.UserInfo;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.User;
import com.tutoringplatform.user.availability.model.AvailabilityException;
import com.tutoringplatform.user.availability.model.RecurringAvailability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOMapper {

    @Autowired
    private AppProperties appProperties;

    // ========== AUTH RESPONSES ==========

    public AuthResponse toAuthResponse(User user, double balance, double hourlyRate) {
        AuthResponse response = new AuthResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setUserType(user.getUserType().getDisplayName());
        response.setTimeZoneId(user.getTimeZoneId());
        response.setProfilePictureUrl(buildProfilePictureUrl(user.getProfilePictureId()));
        response.setBalance(balance); // Only for students
        response.setHourlyRate(hourlyRate); // Only for tutors
        return response;
    }

    // ========== DASHBOARD RESPONSES ==========

    public StudentDashboardResponse toStudentDashboardResponse(
            UserProfile profile,
            DashboardStats stats,
            List<BookingDetailResponse> upcomingBookings) {

        StudentDashboardResponse response = new StudentDashboardResponse();
        response.setProfile(profile);
        response.setStats(stats);
        response.setUpcomingBookings(upcomingBookings);

        return response;
    }

    public TutorDashboardResponse toTutorDashboardResponse(
            UserProfile profile,
            DashboardStats stats,
            List<BookingDetailResponse> upcomingBookings,
            List<ReviewResponse> recentReviews,
            List<BookingDetailResponse> todaysSchedule) {

        TutorDashboardResponse response = new TutorDashboardResponse();
        response.setProfile(profile);
        response.setStats(stats);
        response.setUpcomingBookings(upcomingBookings);
        response.setTodaysSchedule(todaysSchedule);

        return response;
    }

    // ========== BOOKING RESPONSES ==========

    public BookingDetailResponse toBookingDetailResponse(
            Booking booking,
            Student student,
            Tutor tutor,
            Payment payment) {

        BookingDetailResponse response = new BookingDetailResponse();
        response.setId(booking.getId());

        // Student info
        UserInfo studentInfo = new UserInfo();
        studentInfo.setId(student.getId());
        studentInfo.setName(student.getName());
        studentInfo.setProfilePictureUrl(buildProfilePictureUrl(student.getProfilePictureId()));
        response.setStudent(studentInfo);

        // Tutor info
        TutorInfo tutorInfo = new TutorInfo();
        tutorInfo.setId(tutor.getId());
        tutorInfo.setName(tutor.getName());
        tutorInfo.setProfilePictureUrl(buildProfilePictureUrl(tutor.getProfilePictureId()));
        tutorInfo.setHourlyRate(tutor.getHourlyRate());
        response.setTutor(tutorInfo);

        // Subject
        response.setSubject(toSubjectResponse(booking.getSubject()));

        response.setDateTime(booking.getDateTime());
        response.setDurationHours(booking.getDurationHours());
        response.setTotalCost(booking.getTotalCost());
        response.setStatus(booking.getStatus().toString());

        // Payment info (if exists)
        if (payment != null) {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setStatus(payment.getStatus().toString());
            paymentInfo.setPaidAt(payment.getTimestamp());
            response.setPayment(paymentInfo);
        }

        return response;
    }

    public BookingListResponse toBookingListResponse(
            List<BookingDetailResponse> upcomingBookings,
            List<BookingDetailResponse> pastBookings,
            List<BookingDetailResponse> cancelledBookings) {

        BookingListResponse response = new BookingListResponse();
        response.setUpcomingBookings(upcomingBookings);
        response.setPastBookings(pastBookings);
        response.setCancelledBookings(cancelledBookings);
        return response;
    }

    // ========== PROFILE RESPONSES ==========

    public TutorProfileResponse toTutorProfileResponse(
            Tutor tutor,
            double averageRating,
            int totalReviews,
            List<SubjectResponse> subjects,
            List<RecurringAvailability> availability,
            int completedSessions,
            java.time.LocalDate joinedDate) {

        TutorProfileResponse response = new TutorProfileResponse();
        response.setId(tutor.getId());
        response.setName(tutor.getName());
        response.setEmail(tutor.getEmail());
        response.setProfilePictureUrl(buildProfilePictureUrl(tutor.getProfilePictureId()));
        response.setHourlyRate(tutor.getHourlyRate());
        response.setDescription(tutor.getDescription());
        response.setRating(averageRating);
        response.setTotalReviews(totalReviews);
        response.setSubjects(subjects);
        response.setAvailability(availability);
        response.setCompletedSessions(completedSessions);
        response.setJoinedDate(joinedDate);

        return response;
    }

    public StudentProfileResponse toStudentProfileResponse(
            Student student,
            java.time.LocalDate joinedDate,
            int totalSessions) {

        StudentProfileResponse response = new StudentProfileResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setProfilePictureUrl(buildProfilePictureUrl(student.getProfilePictureId()));
        response.setBalance(student.getBalance());
        response.setTimeZoneId(student.getTimeZoneId());
        response.setJoinedDate(joinedDate);
        response.setTotalSessions(totalSessions);

        return response;
    }

    // ========== SEARCH RESPONSES ==========

    public TutorSearchResultsResponse toTutorSearchResultsResponse(
            List<TutorSearchResultInfo> results,
            int totalCount,
            SearchFilters appliedFilters) {

        TutorSearchResultsResponse response = new TutorSearchResultsResponse();
        response.setResults(results);
        response.setTotalCount(totalCount);
        response.setFilters(appliedFilters);
        return response;
    }

    public TutorSearchResultInfo toTutorSearchResult(
            Tutor tutor,
            double rating,
            int reviewCount,
            String shortDescription,
            java.time.LocalDateTime nextAvailable) {

        TutorSearchResultInfo result = new TutorSearchResultInfo();
        result.setId(tutor.getId());
        result.setName(tutor.getName());
        result.setProfilePictureUrl(buildProfilePictureUrl(tutor.getProfilePictureId()));
        result.setHourlyRate(tutor.getHourlyRate());
        result.setRating(rating);
        result.setReviewCount(reviewCount);
        result.setSubjects(tutor.getSubjects().stream()
                .map(this::toSubjectResponse)
                .collect(Collectors.toList()));
        result.setShortDescription(shortDescription);
        result.setNextAvailable(nextAvailable);

        return result;
    }

    // ========== SUBJECT RESPONSES ==========

    public SubjectResponse toSubjectResponse(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setCategory(subject.getCategory());
        return response;
    }

    public SubjectListResponse toSubjectListResponse(List<CategorySubjects> categorizedSubjects) {
        SubjectListResponse response = new SubjectListResponse();
        response.setSubjects(categorizedSubjects);
        return response;
    }

    // ========== REVIEW RESPONSES ==========

    public ReviewResponse toReviewResponse(
            Review review,
            Student student,
            Tutor tutor) {

        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());

        // Student info
        UserInfo studentInfo = new UserInfo();
        studentInfo.setId(student.getId());
        studentInfo.setName(student.getName());
        studentInfo.setProfilePictureUrl(buildProfilePictureUrl(student.getProfilePictureId()));
        response.setStudentInfo(studentInfo);

        // Tutor info (minimal)
        UserInfo tutorInfo = new UserInfo();
        tutorInfo.setId(tutor.getId());
        tutorInfo.setName(tutor.getName());
        response.setTutorInfo(tutorInfo);

        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getTimestamp());

        return response;
    }


    // ========== AVAILABILITY RESPONSES ==========

    public AvailabilityResponse toAvailabilityResponse(
            String tutorId,
            java.time.ZoneId timeZone,
            List<RecurringAvailability> regularSchedule,
            List<AvailabilityException> exceptions,
            java.time.LocalDateTime nextAvailableSlot) {

        AvailabilityResponse response = new AvailabilityResponse();
        response.setTutorId(tutorId);
        response.setTimeZone(timeZone.toString());
        response.setRegularSchedule(regularSchedule);
        response.setExceptions(exceptions);
        response.setNextAvailableSlot(nextAvailableSlot);
        return response;
    }

    // ========== NOTIFICATION RESPONSES ==========

    public NotificationResponse toNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType().toString());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setActionUrl(notification.getActionUrl());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        response.setTimeAgo(calculateTimeAgo(notification.getCreatedAt()));
        return response;
    }

    private String calculateTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 1)
            return "just now";
        if (minutes < 60)
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";

        long hours = minutes / 60;
        if (hours < 24)
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";

        long days = hours / 24;
        if (days < 7)
            return days + " day" + (days > 1 ? "s" : "") + " ago";

        long weeks = days / 7;
        if (weeks < 4)
            return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";

        long months = days / 30;
        return months + " month" + (months > 1 ? "s" : "") + " ago";
    }

    // ========== VALUE RESPONSE ==========

    public <T> ValueResponse<T> toValueResponse(T value) {
        ValueResponse<T> response = new ValueResponse<>();
        response.setValue(value);
        return response;
    }

    public <T> ValueResponse<T> toValueResponse(T value, String message) {
        ValueResponse<T> response = new ValueResponse<>();
        response.setValue(value);
        response.setMessage(message);
        return response;
    }

    // ========== HELPER METHODS ==========

    public String buildProfilePictureUrl(String profilePictureId) {
        if (profilePictureId == null) {
            return appProperties.getApi().getFilesBaseUrl() + "default-avatar";
        }
        return appProperties.getApi().getFilesBaseUrl() + profilePictureId;
    }
}