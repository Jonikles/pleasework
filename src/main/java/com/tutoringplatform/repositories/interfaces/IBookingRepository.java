package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Booking;

public interface IBookingRepository {
    Booking findById(String id);
    List<Booking> findAll();
    List<Booking> findByStudentId(String studentId);
    List<Booking> findByTutorId(String tutorId);
    List<Booking> findByStatus(Booking.BookingStatus status);
    boolean hasCompletedBooking(String studentId, String tutorId);
    void save(Booking booking);
    void update(Booking booking);
    void delete(String id);
}