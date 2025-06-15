package com.tutoringplatform.dto.response;

import com.tutoringplatform.dto.response.info.BookingInfo;
import java.time.LocalDateTime;

public class TransactionRecord {
    private String id;
    private TransactionType type;
    private double amount;
    private LocalDateTime date;
    private BookingInfo booking;

    public enum TransactionType {
        PAYMENT,
        REFUND
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BookingInfo getBooking() {
        return booking;
    }

    public void setBooking(BookingInfo booking) {
        this.booking = booking;
    }
}