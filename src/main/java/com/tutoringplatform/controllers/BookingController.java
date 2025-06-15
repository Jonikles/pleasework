package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.CreateBookingRequest;
import com.tutoringplatform.dto.request.UpdateBookingRequest;
import com.tutoringplatform.dto.response.BookingDetailResponse;
import com.tutoringplatform.dto.response.BookingListResponse;
import com.tutoringplatform.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request) {
        try {
            BookingDetailResponse booking = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable String id) {
        try {
            BookingDetailResponse booking = bookingService.getBookingDetails(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentBookings(@PathVariable String studentId) {
        try {
            BookingListResponse bookings = bookingService.getStudentBookingList(studentId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorBookings(@PathVariable String tutorId) {
        try {
            BookingListResponse bookings = bookingService.getTutorBookingList(tutorId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable String id, @RequestBody UpdateBookingRequest request) {
        try {
            BookingDetailResponse booking = bookingService.updateBooking(id, request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable String id) {
        try {
            BookingDetailResponse booking = bookingService.confirmBooking(id);
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
}
