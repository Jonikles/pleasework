// FILE: src/main/java/com/tutoringplatform/services/AvailabilityService.java
package com.tutoringplatform.services;

import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.availability.*;
import com.tutoringplatform.repositories.interfaces.IAvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.*;
import java.util.*;

@Service
public class AvailabilityService {

    @Autowired
    private IAvailabilityRepository availabilityRepository;

    @Autowired
    private TutorService tutorService;

    public TutorAvailability getAvailability(String tutorId) throws Exception {
        TutorAvailability availability = availabilityRepository.findByTutorId(tutorId);

        if (availability == null) {
            Tutor tutor = tutorService.findById(tutorId);
            availability = new TutorAvailability(tutorId, tutor.getTimeZone());
            availabilityRepository.save(availability);
        }

        return availability;
    }

    public void addRecurringAvailability(String tutorId, DayOfWeek day, LocalTime start, LocalTime end)
            throws Exception {
        TutorAvailability availability = getAvailability(tutorId);

        // Check for overlaps
        for (RecurringAvailability existing : availability.getRecurringSlots()) {
            if (existing.getDayOfWeek() == day) {
                if (!(end.isBefore(existing.getStartTime()) || start.isAfter(existing.getEndTime()))) {
                    throw new IllegalArgumentException("Time slot overlaps with existing availability");
                }
            }
        }

        availability.getRecurringSlots().add(new RecurringAvailability(day, start, end));
        availabilityRepository.update(availability);
    }

    public void removeRecurringAvailability(String tutorId, DayOfWeek day, LocalTime start, LocalTime end)
            throws Exception {
        TutorAvailability availability = getAvailability(tutorId);

        availability.getRecurringSlots().removeIf(slot -> slot.getDayOfWeek() == day &&
                slot.getStartTime().equals(start) &&
                slot.getEndTime().equals(end));

        availabilityRepository.update(availability);
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
        availabilityRepository.update(availability);
    }

    public boolean isAvailable(String tutorId, ZonedDateTime start, ZonedDateTime end, ZoneId studentTimeZone)
            throws Exception {
        TutorAvailability availability = getAvailability(tutorId);
        return availability.isAvailable(start, end, studentTimeZone);
    }

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
}