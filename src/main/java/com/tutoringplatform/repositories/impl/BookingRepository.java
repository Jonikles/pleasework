package com.tutoringplatform.repositories.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.tutoringplatform.models.Booking;
import com.tutoringplatform.repositories.interfaces.IBookingRepository;

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