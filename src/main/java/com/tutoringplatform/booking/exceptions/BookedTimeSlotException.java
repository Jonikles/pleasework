package com.tutoringplatform.booking.exceptions;

import java.time.ZonedDateTime;

public class BookedTimeSlotException extends BookingException {
    private final String tutorId;
    private final ZonedDateTime startTime;
    private final ZonedDateTime endTime;

    public BookedTimeSlotException(String tutorId, ZonedDateTime startTime, ZonedDateTime endTime) {
        super("BOOKED_TIME_SLOT", "Time slot already booked for tutor " + tutorId + " from " + startTime + " to " + endTime);
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
