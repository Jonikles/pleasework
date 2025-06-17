package com.tutoringplatform.booking;

import java.time.LocalDateTime;
import java.util.List;

import com.tutoringplatform.shared.IRepository;

public interface IBookingRepository extends IRepository<Booking> {
    List<Booking> findByStudentId(String studentId);
    List<Booking> findByTutorId(String tutorId);
    List<Booking> findByStatus(Booking.BookingStatus status);
    boolean hasCompletedBooking(String studentId, String tutorId);
    List<Booking> findByTutorIdAndDateTimeRange(String tutorId, LocalDateTime startTime, LocalDateTime endTime);
    List<Booking> findByTutorIdAndSubjectId(String tutorId, String subjectId);
    List<Booking> findByStudentIdAndTutorIdAndStatus(String studentId, String tutorId, Booking.BookingStatus status);
}