package com.tutoringplatform.user.availability.model;

import java.time.*;
import java.util.*;

public class TutorAvailability {
    private String tutorId;
    private ZoneId timeZone;
    private List<RecurringAvailability> recurringSlots;
    private List<AvailabilityException> exceptions;

    public TutorAvailability(String tutorId, ZoneId timeZone) {
        this.tutorId = tutorId;
        this.timeZone = timeZone;
        this.recurringSlots = new ArrayList<>();
        this.exceptions = new ArrayList<>();
    }

    public boolean isAvailable(ZonedDateTime requestedStart, ZonedDateTime requestedEnd, ZoneId studentTimeZone) {
        ZonedDateTime tutorStart = requestedStart.withZoneSameInstant(timeZone);
        ZonedDateTime tutorEnd = requestedEnd.withZoneSameInstant(timeZone);

        for (AvailabilityException exception : exceptions) {
            if (exception.covers(tutorStart, tutorEnd)) {
                return exception.isAvailable();
            }
        }

        DayOfWeek dayOfWeek = tutorStart.getDayOfWeek();
        LocalTime startTime = tutorStart.toLocalTime();
        LocalTime endTime = tutorEnd.toLocalTime();

        if (!tutorEnd.toLocalDate().equals(tutorStart.toLocalDate())) {
            return false;
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