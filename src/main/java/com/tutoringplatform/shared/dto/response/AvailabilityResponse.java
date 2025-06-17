package com.tutoringplatform.shared.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.tutoringplatform.user.availability.model.AvailabilityException;
import com.tutoringplatform.user.availability.model.RecurringAvailability;

public class AvailabilityResponse {
    private String tutorId;
    private String timeZone;
    private List<RecurringAvailability> regularSchedule;
    private List<AvailabilityException> exceptions;
    private LocalDateTime nextAvailableSlot;

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<RecurringAvailability> getRegularSchedule() {
        return regularSchedule;
    }

    public void setRegularSchedule(List<RecurringAvailability> regularSchedule) {
        this.regularSchedule = regularSchedule;
    }

    public List<AvailabilityException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<AvailabilityException> exceptions) {
        this.exceptions = exceptions;
    }

    public LocalDateTime getNextAvailableSlot() {
        return nextAvailableSlot;
    }

    public void setNextAvailableSlot(LocalDateTime nextAvailableSlot) {
        this.nextAvailableSlot = nextAvailableSlot;
    }
}
