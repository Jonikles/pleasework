// FILE: src/main/java/com/tutoringplatform/dto/response/BookingResponse.java
package com.tutoringplatform.dto.response;

import java.time.LocalDateTime;

public class BookingResponse {
    private String id;
    private String studentId;
    private String tutorId;
    private SubjectResponse subject;
    private LocalDateTime dateTime;
    private int durationHours;
    private double totalCost;
    private String status;
    private String studentTimeZoneId;
    private String tutorTimeZoneId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public SubjectResponse getSubject() {
        return subject;
    }

    public void setSubject(SubjectResponse subject) {
        this.subject = subject;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentTimeZoneId() {
        return studentTimeZoneId;
    }

    public void setStudentTimeZoneId(String studentTimeZoneId) {
        this.studentTimeZoneId = studentTimeZoneId;
    }

    public String getTutorTimeZoneId() {
        return tutorTimeZoneId;
    }

    public void setTutorTimeZoneId(String tutorTimeZoneId) {
        this.tutorTimeZoneId = tutorTimeZoneId;
    }
}