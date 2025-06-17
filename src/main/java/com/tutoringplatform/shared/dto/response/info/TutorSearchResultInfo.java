package com.tutoringplatform.shared.dto.response.info;

import java.time.LocalDateTime;
import java.util.List;

import com.tutoringplatform.shared.dto.response.SubjectResponse;

public class TutorSearchResultInfo {
    private String id;
    private String name;
    private String profilePictureUrl;
    private double hourlyRate;
    private double rating;
    private int reviewCount;
    private List<SubjectResponse> subjects;
    private String shortDescription;
    private LocalDateTime nextAvailable;

    // All getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<SubjectResponse> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectResponse> subjects) {
        this.subjects = subjects;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalDateTime getNextAvailable() {
        return nextAvailable;
    }

    public void setNextAvailable(LocalDateTime nextAvailable) {
        this.nextAvailable = nextAvailable;
    }
}