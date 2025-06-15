package com.tutoringplatform.dto.request;

import java.time.LocalDateTime;

public class UpdateBookingRequest {
    private LocalDateTime dateTime;
    private int durationHours;

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
}