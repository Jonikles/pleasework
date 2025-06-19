package com.tutoringplatform.shared.dto.response;

import java.util.List;

public class TutorDashboardResponse {
    private UserProfile profile;
    private DashboardStats stats;
    private List<BookingDetailResponse> upcomingBookings;
    private List<BookingDetailResponse> todaysSchedule;

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

    public List<BookingDetailResponse> getTodaysSchedule() {
        return todaysSchedule;
    }

    public void setTodaysSchedule(List<BookingDetailResponse> todaysSchedule) {
        this.todaysSchedule = todaysSchedule;
    }
}