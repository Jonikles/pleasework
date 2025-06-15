package com.tutoringplatform.util;

import com.tutoringplatform.dto.request.*;
import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.models.*;
import com.tutoringplatform.models.availability.RecurringAvailability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import com.tutoringplatform.config.AppProperties;

@Component
public class DTOMapper {

    @Autowired
    private AppProperties appProperties;

    // ========== AUTH RESPONSES ==========

    public AuthResponse toAuthResponse(User user, Double balance, Double hourlyRate) {
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
            Student student,
            DashboardStats stats,
            List<BookingDetailResponse> upcomingBookings,
            List<BookingDetailResponse> recentCompletedBookings,
            List<SubjectResponse> availableSubjects) {

        StudentDashboardResponse response = new StudentDashboardResponse();

        // Profile
        UserProfile profile = new UserProfile();
        profile.setName(student.getName());
        profile.setBalance(student.getBalance());
        profile.setProfilePictureUrl(buildProfilePictureUrl(student.getProfilePictureId()));
        response.setProfile(profile);

        response.setStats(stats);
        response.setUpcomingBookings(upcomingBookings);
        response.setRecentCompletedBookings(recentCompletedBookings);
        response.setAvailableSubjects(availableSubjects);

        return response;
    }

    public TutorDashboardResponse toTutorDashboardResponse(
            Tutor tutor,
            DashboardStats stats,
            List<BookingDetailResponse> upcomingBookings,
            List<ReviewResponse> recentReviews,
            List<BookingDetailResponse> todaysSchedule) {

        TutorDashboardResponse response = new TutorDashboardResponse();

        // Profile
        UserProfile profile = new UserProfile();
        profile.setName(tutor.getName());
        profile.setHourlyRate(tutor.getHourlyRate());
        profile.setProfilePictureUrl(buildProfilePictureUrl(tutor.getProfilePictureId()));
        response.setProfile(profile);

        response.setStats(stats);
        response.setUpcomingBookings(upcomingBookings);
        response.setRecentReviews(recentReviews);
        response.setTodaysSchedule(todaysSchedule);

        return response;
    }

    // ========== BOOKING RESPONSES ==========

    public BookingDetailResponse toBookingDetailResponse(
            Booking booking,
            Student student,
            Tutor tutor,
            Payment payment,
            Review review) {

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

        // Review info (if exists)
        if (review != null) {
            ReviewInfo reviewInfo = new ReviewInfo();
            reviewInfo.setRating(review.getRating());
            reviewInfo.setComment(review.getComment());
            response.setReview(reviewInfo);
        }

        // Meeting link would be set by service based on status
        response.setMeetingLink(null);

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
            Double averageRating,
            Integer totalReviews,
            List<SubjectResponse> subjects,
            List<RecurringAvailability> availability,
            List<ReviewResponse> reviews,
            Integer completedSessions,
            String responseTime,
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
        response.setReviews(reviews);
        response.setCompletedSessions(completedSessions);
        response.setResponseTime(responseTime);
        response.setJoinedDate(joinedDate);

        return response;
    }

    public StudentProfileResponse toStudentProfileResponse(
            Student student,
            java.time.LocalDate joinedDate,
            Integer totalSessions,
            List<SubjectInfo> favoriteSubjects,
            List<TutorInfo> favoriteTutors) {

        StudentProfileResponse response = new StudentProfileResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setProfilePictureUrl(buildProfilePictureUrl(student.getProfilePictureId()));
        response.setBalance(student.getBalance());
        response.setTimeZoneId(student.getTimeZoneId());
        response.setJoinedDate(joinedDate);
        response.setTotalSessions(totalSessions);
        response.setFavoriteSubjects(favoriteSubjects);
        response.setFavoriteTutors(favoriteTutors);

        return response;
    }

    // ========== SEARCH RESPONSES ==========

    public TutorSearchResultsResponse toTutorSearchResultsResponse(
            List<TutorSearchResult> results,
            Integer totalCount,
            SearchFilters appliedFilters) {

        TutorSearchResultsResponse response = new TutorSearchResultsResponse();
        response.setResults(results);
        response.setTotalCount(totalCount);
        response.setFilters(appliedFilters);
        return response;
    }

    public TutorSearchResult toTutorSearchResult(
            Tutor tutor,
            Double rating,
            Integer reviewCount,
            String shortDescription,
            java.time.LocalDateTime nextAvailable,
            boolean isOnline) {

        TutorSearchResult result = new TutorSearchResult();
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
        result.setOnline(isOnline);

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
            Tutor tutor,
            java.time.LocalDateTime bookingDate) {

        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());

        // Student info
        UserInfo studentInfo = new UserInfo();
        studentInfo.setId(student.getId());
        studentInfo.setName(student.getName());
        studentInfo.setProfilePictureUrl(buildProfilePictureUrl(student.getProfilePictureId()));
        response.setStudent(studentInfo);

        // Tutor info (minimal)
        UserInfo tutorInfo = new UserInfo();
        tutorInfo.setId(tutor.getId());
        tutorInfo.setName(tutor.getName());
        response.setTutor(tutorInfo);

        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getTimestamp());
        response.setBookingDate(bookingDate);

        return response;
    }

    // ========== PAYMENT RESPONSES ==========

    public PaymentHistoryResponse toPaymentHistoryResponse(
            List<TransactionRecord> transactions,
            Double totalSpent,
            Double currentBalance) {

        PaymentHistoryResponse response = new PaymentHistoryResponse();
        response.setTransactions(transactions);
        response.setTotalSpent(totalSpent);
        response.setCurrentBalance(currentBalance);
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

    // ========== VALUE RESPONSE ==========

    public <T> ValueResponse<T> toValueResponse(T value) {
        return new ValueResponse<>(value);
    }

    public <T> ValueResponse<T> toValueResponse(T value, String message) {
        return new ValueResponse<>(value, message);
    }

    // ========== HELPER METHODS ==========

    private String buildProfilePictureUrl(String profilePictureId) {
        if (profilePictureId == null) {
            return appProperties.getApi().getFilesBaseUrl() + "default-avatar";
        }
        return appProperties.getApi().getFilesBaseUrl() + profilePictureId;
    }
}