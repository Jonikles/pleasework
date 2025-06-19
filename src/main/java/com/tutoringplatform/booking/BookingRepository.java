package com.tutoringplatform.booking;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class BookingRepository implements IBookingRepository {
    private Map<String, Booking> bookings = new HashMap<>();

    @Override
    public Booking findById(String id) {
        return bookings.get(id);
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public List<Booking> findByStudentId(String studentId) {
        return bookings.values().stream()
                .filter(b -> b.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByTutorId(String tutorId) {
        return bookings.values().stream()
                .filter(b -> b.getTutorId().equals(tutorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByStatus(Booking.BookingStatus status) {
        return bookings.values().stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasCompletedBooking(String studentId, String tutorId) {
        for (Booking booking : bookings.values()) {
            if (booking.getStudentId().equals(studentId) &&
                    booking.getTutorId().equals(tutorId) &&
                    booking.getStatus() == Booking.BookingStatus.COMPLETED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Booking> findByTutorIdAndDateTimeRange(String tutorId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookings.values().stream()
                .filter(b -> b.getTutorId().equals(tutorId) &&
                        b.getDateTime().isAfter(startTime) &&
                        b.getDateTime().isBefore(endTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByTutorIdAndSubjectId(String tutorId, String subjectId) {
        return bookings.values().stream()
                .filter(b -> b.getTutorId().equals(tutorId) &&
                        b.getSubject().getId().equals(subjectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByStudentIdAndTutorIdAndStatus(String studentId, String tutorId, Booking.BookingStatus status) {
        return bookings.values().stream()
                .filter(b -> b.getStudentId().equals(studentId) &&
                        b.getTutorId().equals(tutorId) &&
                        b.getStatus() == status)
                .collect(Collectors.toList());
    }


    @Override
    public void save(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    @Override
    public void update(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    @Override
    public void delete(String id) {
        bookings.remove(id);
    }
}