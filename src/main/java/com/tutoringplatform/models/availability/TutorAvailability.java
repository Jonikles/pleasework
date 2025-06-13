// FILE: src/main/java/com/tutoringplatform/models/availability/TutorAvailability.java
package com.tutoringplatform.models.availability;

import java.time.*;
import java.util.*;

public class TutorAvailability {
    private String tutorId;
    private ZoneId timeZone; // Tutor's time zone
    private List<RecurringAvailability> recurringSlots;
    private List<AvailabilityException> exceptions;

    public TutorAvailability(String tutorId, ZoneId timeZone) {
        this.tutorId = tutorId;
        this.timeZone = timeZone;
        this.recurringSlots = new ArrayList<>();
        this.exceptions = new ArrayList<>();
    }

    // Check availability in the STUDENT's timezone
    public boolean isAvailable(ZonedDateTime requestedStart, ZonedDateTime requestedEnd, ZoneId studentTimeZone) {
        // Convert student's request to tutor's timezone
        ZonedDateTime tutorStart = requestedStart.withZoneSameInstant(timeZone);
        ZonedDateTime tutorEnd = requestedEnd.withZoneSameInstant(timeZone);

        // Check exceptions first (vacations, special unavailability)
        for (AvailabilityException exception : exceptions) {
            if (exception.covers(tutorStart, tutorEnd)) {
                return exception.isAvailable();
            }
        }

        // Check recurring availability
        DayOfWeek dayOfWeek = tutorStart.getDayOfWeek();
        LocalTime startTime = tutorStart.toLocalTime();
        LocalTime endTime = tutorEnd.toLocalTime();

        // Handle sessions that cross midnight
        if (!tutorEnd.toLocalDate().equals(tutorStart.toLocalDate())) {
            // Session spans multiple days - need more complex logic
            return false; // For now, don't allow cross-day bookings
        }

        for (RecurringAvailability slot : recurringSlots) {
            if (slot.getDayOfWeek() == dayOfWeek &&
                    slot.contains(startTime, endTime)) {
                return true;
            }
        }

        return false;
    }

    
    public String getTutorId() {
        return tutorId;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public List<RecurringAvailability> getRecurringSlots() {
        return recurringSlots;
    }

    public List<AvailabilityException> getExceptions() {
        return exceptions;
    }
}