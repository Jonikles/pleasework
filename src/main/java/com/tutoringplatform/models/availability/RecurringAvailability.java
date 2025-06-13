// FILE: src/main/java/com/tutoringplatform/models/availability/RecurringAvailability.java
package com.tutoringplatform.models.availability;

import java.time.*;

public class RecurringAvailability {
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public RecurringAvailability(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean contains(LocalTime requestStart, LocalTime requestEnd) {
        return !requestStart.isBefore(startTime) && !requestEnd.isAfter(endTime);
    }

    // Getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}