package com.tutoringplatform.booking;

import com.tutoringplatform.shared.dto.request.CreateBookingRequest;
import com.tutoringplatform.shared.dto.request.UpdateBookingRequest;
import com.tutoringplatform.shared.dto.response.BookingDetailResponse;
import com.tutoringplatform.shared.dto.response.BookingListResponse;
import com.tutoringplatform.exceptions.InsufficientBalanceException;
import com.tutoringplatform.exceptions.BookingNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final Logger logger = LoggerFactory.getLogger(BookingController.class);
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
    public ResponseEntity<BookingDetailResponse> confirmBooking(@PathVariable String id) throws InsufficientBalanceException, BookingNotFoundException {
        logger.info("API call: confirm booking {}", id);
        BookingDetailResponse response = bookingService.confirmBooking(id);
        return ResponseEntity.ok(response);
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
