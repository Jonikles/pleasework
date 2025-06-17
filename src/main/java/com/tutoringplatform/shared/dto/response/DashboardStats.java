package com.tutoringplatform.shared.dto.response;

public class DashboardStats {
    private int totalSessions;
    private int completedSessions;
    private int upcomingSessions;
    private double totalEarnings; // Tutors only
    private double thisMonthEarnings; // Tutors only
    private double averageRating; // Tutors only
    private int totalReviews; // Tutors only

    // All getters and setters
    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public int getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(int upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getThisMonthEarnings() {
        return thisMonthEarnings;
    }

    public void setThisMonthEarnings(double thisMonthEarnings) {
        this.thisMonthEarnings = thisMonthEarnings;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }
}
