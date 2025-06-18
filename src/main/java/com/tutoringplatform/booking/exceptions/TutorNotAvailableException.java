package com.tutoringplatform.booking.exceptions;

import java.time.ZonedDateTime;

public class TutorNotAvailableException extends BookingException {
    private final String tutorId;
    private final ZonedDateTime startTime;
    private final ZonedDateTime endTime;

    public TutorNotAvailableException(String tutorId, ZonedDateTime startTime, ZonedDateTime endTime) { 
        super("TUTOR_NOT_AVAILABLE", "Tutor " + tutorId + " is not available from " + startTime + " to " + endTime);
        this.tutorId = tutorId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTutorId() {
        return tutorId;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }
}
