// FILE: src/main/java/com/tutoringplatform/dto/response/StudentDashboardResponse.java
package com.tutoringplatform.dto.response;

import java.util.List;

public class StudentDashboardResponse {
    private StudentResponse student;
    private List<EnrichedBookingResponse> upcomingBookings;
    private List<EnrichedBookingResponse> pastBookings;
    private List<SubjectResponse> availableSubjects;

    // Getters and setters
    public StudentResponse getStudent() {
        return student;
    }

    public void setStudent(StudentResponse student) {
        this.student = student;
    }

    public List<EnrichedBookingResponse> getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(List<EnrichedBookingResponse> upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public List<EnrichedBookingResponse> getPastBookings() {
        return pastBookings;
    }

    public void setPastBookings(List<EnrichedBookingResponse> pastBookings) {
        this.pastBookings = pastBookings;
    }

    public List<SubjectResponse> getAvailableSubjects() {
        return availableSubjects;
    }

    public void setAvailableSubjects(List<SubjectResponse> availableSubjects) {
        this.availableSubjects = availableSubjects;
    }
}