package com.tutoringplatform.dto.response;

import java.util.List;

public class TutorDashboardResponse {
    private TutorResponse tutor;
    private List<EnrichedBookingResponse> upcomingBookings;
    private List<EnrichedBookingResponse> recentBookings;
    private List<ReviewResponse> recentReviews;
    private DashboardStats stats;

    public DashboardStats getStats() {
        return stats;
    }

    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }

    public TutorResponse getTutor() {
        return tutor;
    }

    public void setTutor(TutorResponse tutor) {
        this.tutor = tutor;
    }

    public List<EnrichedBookingResponse> getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(List<EnrichedBookingResponse> upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public List<EnrichedBookingResponse> getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(List<EnrichedBookingResponse> recentBookings) {
        this.recentBookings = recentBookings;
    }

    public List<ReviewResponse> getRecentReviews() {
        return recentReviews;
    }

    public void setRecentReviews(List<ReviewResponse> recentReviews) {
        this.recentReviews = recentReviews;
    }
}