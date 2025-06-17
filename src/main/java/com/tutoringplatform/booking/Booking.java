package com.tutoringplatform.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.subject.Subject;

public class Booking {
    private String id;
    private String studentId;
    private String tutorId;
    private Subject subject;
    private LocalDateTime dateTime;
    private int durationHours;
    private double totalCost;
    private BookingStatus status;
    private Payment payment;

    public enum BookingStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    public Booking(String studentId, String tutorId, Subject subject, LocalDateTime dateTime, int durationHours, double hourlyRate) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.subject = subject;
        this.dateTime = dateTime;
        this.durationHours = durationHours;
        this.totalCost = hourlyRate * durationHours;
        this.status = BookingStatus.PENDING;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getTutorId() { return tutorId; }
    public void setTutorId(String tutorId) { this.tutorId = tutorId; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}