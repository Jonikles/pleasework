package com.tutoringplatform.user.availability;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.shared.dto.request.TutorAvailabilityRequest;
import com.tutoringplatform.shared.dto.response.AvailabilityResponse;
import com.tutoringplatform.user.availability.model.*;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.user.tutor.Tutor;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.util.*;

@Service
public class AvailabilityService {

    private final IAvailabilityRepository availabilityRepository;
    private final TutorService tutorService;
    private final IBookingRepository bookingRepository;

    @Autowired
    public AvailabilityService(IAvailabilityRepository availabilityRepository,
            TutorService tutorService,
            IBookingRepository bookingRepository) {
        this.availabilityRepository = availabilityRepository;
        this.tutorService = tutorService;
        this.bookingRepository = bookingRepository;
    }

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

        // First check basic availability (recurring slots and exceptions)
        if (!availability.isAvailable(start, end, studentTimeZone)) {
            return false;
        }

        // Now check for conflicting bookings (excluding cancelled ones)
        LocalDateTime startLocal = start.withZoneSameInstant(availability.getTimeZone()).toLocalDateTime();
        LocalDateTime endLocal = end.withZoneSameInstant(availability.getTimeZone()).toLocalDateTime();

        List<Booking> existingBookings = bookingRepository.findByTutorIdAndDateTimeRange(
                tutorId, startLocal, endLocal);

        // Filter out cancelled bookings - the slot is available if booking was
        // cancelled
        for (Booking booking : existingBookings) {
            if (booking.getStatus() != Booking.BookingStatus.CANCELLED) {
                // Check for time overlap with non-cancelled bookings
                LocalDateTime bookingEnd = booking.getDateTime().plusHours(booking.getDurationHours());
                if (!(endLocal.isBefore(booking.getDateTime()) || startLocal.isAfter(bookingEnd))) {
                    return false;
                }
            }
        }

        return true;
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

    // New methods required by TutorController
    public AvailabilityResponse updateTutorAvailability(String tutorId, TutorAvailabilityRequest request)
            throws Exception {
        if ("ADD".equalsIgnoreCase(request.getAction())) {
            addRecurringAvailability(tutorId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime());
        } else if ("REMOVE".equalsIgnoreCase(request.getAction())) {
            removeRecurringAvailability(tutorId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime());
        } else {
            throw new IllegalArgumentException("Action must be either 'ADD' or 'REMOVE'");
        }

        return getTutorAvailability(tutorId);
    }

    public AvailabilityResponse getTutorAvailability(String tutorId) throws Exception {
        TutorAvailability availability = getAvailability(tutorId);

        AvailabilityResponse response = new AvailabilityResponse();
        response.setTutorId(tutorId);
        response.setTimeZone(availability.getTimeZone().getId());
        response.setRegularSchedule(availability.getRecurringSlots());
        response.setExceptions(availability.getExceptions());

        // Calculate next available slot (simplified implementation)
        LocalDateTime nextSlot = calculateNextAvailableSlot(availability);
        response.setNextAvailableSlot(nextSlot);

        return response;
    }

    private LocalDateTime calculateNextAvailableSlot(TutorAvailability availability) {
        // Simplified implementation - find the next recurring slot from now
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDay = now.getDayOfWeek();

        // Look for the next available slot in the recurring schedule
        for (int daysToAdd = 0; daysToAdd < 7; daysToAdd++) {
            DayOfWeek checkDay = currentDay.plus(daysToAdd);
            LocalDate checkDate = now.toLocalDate().plusDays(daysToAdd);

            for (RecurringAvailability slot : availability.getRecurringSlots()) {
                if (slot.getDayOfWeek() == checkDay) {
                    LocalDateTime slotDateTime = LocalDateTime.of(checkDate, slot.getStartTime());
                    if (slotDateTime.isAfter(now)) {
                        return slotDateTime;
                    }
                }
            }
        }

        // If no slot found in the next week, return null
        return null;
    }
}