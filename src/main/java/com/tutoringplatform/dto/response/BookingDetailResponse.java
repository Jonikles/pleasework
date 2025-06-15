package com.tutoringplatform.dto.response;

import java.time.LocalDateTime;

import com.tutoringplatform.dto.response.info.PaymentInfo;
import com.tutoringplatform.dto.response.info.TutorInfo;
import com.tutoringplatform.dto.response.info.UserInfo;

public class BookingDetailResponse {
    private String id;
    private UserInfo student;
    private TutorInfo tutor;
    private SubjectResponse subject;
    private LocalDateTime dateTime;
    private int durationHours;
    private double totalCost;
    private String status;
    private PaymentInfo payment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInfo getStudent() {
        return student;
    }

    public void setStudent(UserInfo student) {
        this.student = student;
    }

    public TutorInfo getTutor() {
        return tutor;
    }

    public void setTutor(TutorInfo tutor) {
        this.tutor = tutor;
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

    public PaymentInfo getPayment() {
        return payment;
    }

    public void setPayment(PaymentInfo payment) {
        this.payment = payment;
    }
}