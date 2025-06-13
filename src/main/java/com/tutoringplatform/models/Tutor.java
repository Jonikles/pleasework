package com.tutoringplatform.models;

import java.util.*;
import java.time.ZoneId;

public class Tutor extends User {
    private List<Subject> subjects;
    private double hourlyRate;
    private String description;
    private List<Review> reviewsReceived;
    private double earnings;
    private String timeZoneId;

    public Tutor(String name, String email, String password, double hourlyRate, String description) {
        super(name, email, password, UserType.TUTOR);
        this.subjects = new ArrayList<>();
        this.hourlyRate = hourlyRate;
        this.description = description;
        this.reviewsReceived = new ArrayList<>();
        this.earnings = 0.0;
        this.timeZoneId = ZoneId.systemDefault().getId();
    }

    public double getAverageRating() {
        if (reviewsReceived.isEmpty())
            return 0.0;
        return reviewsReceived.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void addSubject(Subject subject) {
        if (!subjects.contains(subject))
            subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);
    }

    public void addEarnings(double amount) {
        this.earnings += amount;
    }
    
    public List<Review> getReviewsReceived() {
        return reviewsReceived;
    }

    public void addReview(Review review) {
        reviewsReceived.add(review);
    }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getEarnings() { return earnings; }
    public void setEarnings(double earnings) { this.earnings = earnings; }

    public ZoneId getTimeZone() { return ZoneId.of(timeZoneId); }
    public void setTimeZone(ZoneId timeZone) { this.timeZoneId = timeZone.getId(); }

    public String getTimeZoneId() { return timeZoneId; }
    public void setTimeZoneId(String timeZoneId) { this.timeZoneId = timeZoneId; }
}