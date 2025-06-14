package com.tutoringplatform.models;

import java.util.*;

public class Tutor extends User {
    private List<Subject> subjects;
    private double hourlyRate;
    private String description;
    private List<Review> reviewsReceived;
    private double earnings;

    public Tutor(String name, String email, String password, double hourlyRate, String description) {
        super(name, email, password, UserType.TUTOR);
        this.subjects = new ArrayList<>();
        this.hourlyRate = hourlyRate;
        this.description = description;
        this.reviewsReceived = new ArrayList<>();
        this.earnings = 0.0;
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

}