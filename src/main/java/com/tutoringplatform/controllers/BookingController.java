// FILE: src/main/java/com/tutoringplatform/controllers/BookingController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.*;
import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.models.*;
import com.tutoringplatform.services.*;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private DTOMapper dtoMapper;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            Subject subject = subjectService.findById(request.getSubjectId());
            Booking booking = bookingService.createBooking(request.getStudentId(), request.getTutorId(),
                    subject, request.getDateTime(), request.getDurationHours());
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toBookingResponse(booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable String id) {
        try {
            Booking booking = bookingService.findById(id);
            return ResponseEntity.ok(dtoMapper.toBookingResponse(booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentBookings(@PathVariable String studentId) {
        List<Booking> bookings = bookingService.findByStudent(studentId);
        List<BookingResponse> responses = bookings.stream()
                .map(dtoMapper::toBookingResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorBookings(@PathVariable String tutorId) {
        List<Booking> bookings = bookingService.findByTutor(tutorId);
        List<BookingResponse> responses = bookings.stream()
                .map(dtoMapper::toBookingResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable String id, @RequestBody PaymentRequest request) {
        try {
            Booking booking = bookingService.findById(id);
            Payment payment = paymentService.processPayment(request.getStudentId(), id, booking.getTotalCost());
            bookingService.confirmBooking(id, payment);
            Booking updatedBooking = bookingService.findById(id);
            return ResponseEntity.ok(dtoMapper.toBookingResponse(updatedBooking));
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
}