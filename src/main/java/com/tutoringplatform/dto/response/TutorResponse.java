// FILE: src/main/java/com/tutoringplatform/dto/response/TutorResponse.java
package com.tutoringplatform.dto.response;

import java.util.List;
import com.tutoringplatform.models.UserType;
import com.tutoringplatform.models.availability.RecurringAvailability;

public class TutorResponse {
    private String id;
    private String name;
    private String email;
    private UserType userType = UserType.TUTOR;
    private double hourlyRate;
    private String description;
    private double averageRating;
    private double earnings;
    private List<SubjectResponse> subjects;
    private String timeZoneId;
    private List<RecurringAvailability> availability;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    public List<SubjectResponse> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectResponse> subjects) {
        this.subjects = subjects;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public List<RecurringAvailability> getAvailability() {
        return availability;
    }

    public void setAvailability(List<RecurringAvailability> availability) {
        this.availability = availability;
    }
}