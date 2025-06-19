package com.tutoringplatform.shared.dto.response;

import java.util.List;

public class StudentDashboardResponse {
    private UserProfile profile;
    private DashboardStats stats;
    private List<BookingDetailResponse> upcomingBookings;

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public DashboardStats getStats() {
        return stats;
    }

    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }

    public List<BookingDetailResponse> getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(List<BookingDetailResponse> upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }
}