package com.tutoringplatform.user.availability.model;

import java.time.*;

public class AvailabilityException {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public boolean covers(ZonedDateTime start, ZonedDateTime end) {
        LocalDate date = start.toLocalDate();

        if (date.isBefore(startDate) || date.isAfter(endDate)) {
            return false;
        }

        if (startTime == null || endTime == null) {
            return true;
        }

        LocalTime requestStart = start.toLocalTime();
        LocalTime requestEnd = end.toLocalTime();

        return !requestEnd.isBefore(startTime) && !requestStart.isAfter(endTime);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}