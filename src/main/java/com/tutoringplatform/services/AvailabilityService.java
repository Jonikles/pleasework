// FILE: src/main/java/com/tutoringplatform/services/AvailabilityService.java
package com.tutoringplatform.services;

import com.tutoringplatform.models.availability.TutorAvailability;
import com.tutoringplatform.models.availability.RecurringAvailability;
import com.tutoringplatform.models.availability.AvailabilityException;
import com.tutoringplatform.models.Tutor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.*;
import java.util.*;

@Service
public class AvailabilityService {

    // In-memory storage for now - should be in database
    private Map<String, TutorAvailability> availabilityMap = new HashMap<>();

    @Autowired
    private TutorService tutorService;

    public TutorAvailability getAvailability(String tutorId) throws Exception {
        if (!availabilityMap.containsKey(tutorId)) {
            Tutor tutor = tutorService.findById(tutorId);
            TutorAvailability availability = new TutorAvailability(tutorId, tutor.getTimeZone());
            availabilityMap.put(tutorId, availability);
        }
        return availabilityMap.get(tutorId);
    }

    public void addRecurringAvailability(String tutorId, DayOfWeek day, LocalTime start, LocalTime end)
            throws Exception {
        TutorAvailability availability = getAvailability(tutorId);

        // Check for overlaps
        for (RecurringAvailability existing : availability.getRecurringSlots()) {
            if (existing.getDayOfWeek() == day) {
                // Check time overlap
                if (!(end.isBefore(existing.getStartTime()) || start.isAfter(existing.getEndTime()))) {
                    throw new IllegalArgumentException("Time slot overlaps with existing availability");
                }
            }
        }

        availability.getRecurringSlots().add(new RecurringAvailability(day, start, end));
    }

    public void addException(String tutorId, LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime, boolean available) throws Exception {
        TutorAvailability availability = getAvailability(tutorId);
        AvailabilityException exception = new AvailabilityException();
        exception.setStartDate(startDate);
        exception.setEndDate(endDate);
        exception.setStartTime(startTime);
        exception.setEndTime(endTime);
        exception.setAvailable(available);
        availability.getExceptions().add(exception);
    }

    public boolean isAvailable(String tutorId, ZonedDateTime start, ZonedDateTime end, ZoneId studentTimeZone)
            throws Exception {
        TutorAvailability availability = getAvailability(tutorId);
        return availability.isAvailable(start, end, studentTimeZone);
    }

    // For searching available tutors efficiently (would be a database query in
    // production)
    public List<String> findAvailableTutors(List<String> tutorIds, ZonedDateTime start, ZonedDateTime end,
            ZoneId studentTimeZone) {
        List<String> available = new ArrayList<>();
        for (String tutorId : tutorIds) {
            try {
                if (isAvailable(tutorId, start, end, studentTimeZone)) {
                    available.add(tutorId);
                }
            } catch (Exception e) {
                // Log error, skip this tutor
            }
        }
        return available;
    }

    public void removeRecurringAvailability(String tutorId, DayOfWeek day, LocalTime start, LocalTime end)
            throws Exception {
        TutorAvailability availability = getAvailability(tutorId);

        availability.getRecurringSlots().removeIf(slot -> slot.getDayOfWeek() == day &&
                slot.getStartTime().equals(start) &&
                slot.getEndTime().equals(end));
    }
}