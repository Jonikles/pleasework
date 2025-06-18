package com.tutoringplatform.shared.dto.response;

import com.tutoringplatform.shared.dto.response.info.BookingInfo;

import java.time.LocalDateTime;

// Single payment details
public class PaymentResponse {
    private String id;
    private String bookingId;
    private double amount;
    private String status;
    private LocalDateTime timestamp;
    private String transactionId;
    private BookingInfo bookingInfo;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BookingInfo getBookingInfo() {
        return bookingInfo;
    }

    public void setBookingInfo(BookingInfo bookingInfo) { this.bookingInfo = bookingInfo; }
}