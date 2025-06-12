package com.tutoringplatform.controllers;

import com.tutoringplatform.models.*;
import com.tutoringplatform.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            Subject subject = subjectService.findById(request.subjectId);
            Booking booking = bookingService.createBooking(request.studentId, request.tutorId, subject, request.dateTime, request.durationHours);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable String id) {
        try {
            Booking booking = bookingService.findById(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentBookings(@PathVariable String studentId) {
        List<Booking> bookings = bookingService.findByStudent(studentId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorBookings(@PathVariable String tutorId) {
        List<Booking> bookings = bookingService.findByTutor(tutorId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable String id, @RequestBody PaymentRequest request) {
        try {
            Booking booking = bookingService.findById(id);
            Payment payment = paymentService.processPayment(request.studentId, id, booking.getTotalCost());
            bookingService.confirmBooking(id, payment);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable String id) {
        try {
            bookingService.completeBooking(id);
            return ResponseEntity.ok("Booking completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    static class BookingRequest {
        public String studentId;
        public String tutorId;
        public String subjectId;
        public LocalDateTime dateTime;
        public int durationHours;
    }

    static class PaymentRequest {
        public String studentId;
    }
}