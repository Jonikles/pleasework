package com.tutoringplatform.booking;

import com.tutoringplatform.shared.dto.request.CreateBookingRequest;
import com.tutoringplatform.shared.dto.request.UpdateBookingRequest;
import com.tutoringplatform.shared.dto.response.BookingDetailResponse;
import com.tutoringplatform.shared.dto.response.BookingListResponse;
import com.tutoringplatform.booking.exceptions.BookingNotFoundException;
import com.tutoringplatform.booking.exceptions.BookedTimeSlotException;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;
import com.tutoringplatform.subject.exceptions.SubjectNotFoundException;
import com.tutoringplatform.user.student.exceptions.InsufficientBalanceException;
import com.tutoringplatform.user.tutor.exceptions.TutorNotTeachingSubjectException;
import com.tutoringplatform.booking.exceptions.TutorNotAvailableException;
import com.tutoringplatform.user.exceptions.UserNotFoundException;

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
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request)
        throws UserNotFoundException, InsufficientBalanceException,
        SubjectNotFoundException, TutorNotTeachingSubjectException, TutorNotAvailableException,
        BookedTimeSlotException {
        logger.debug("Creating booking: {}", request.getTutorId());
        BookingDetailResponse booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable String id) throws BookingNotFoundException, UserNotFoundException, PaymentNotFoundException {
        logger.debug("Getting booking: {}", id);
        BookingDetailResponse booking = bookingService.getBookingDetails(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentBookings(@PathVariable String studentId) throws UserNotFoundException, PaymentNotFoundException {
        logger.debug("Getting bookings for student: {}", studentId);
        BookingListResponse bookings = bookingService.getStudentBookingList(studentId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorBookings(@PathVariable String tutorId) throws UserNotFoundException, PaymentNotFoundException {
        logger.debug("Getting bookings for tutor: {}", tutorId);
        BookingListResponse bookings = bookingService.getTutorBookingList(tutorId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable String id, @RequestBody UpdateBookingRequest request)
        throws BookingNotFoundException, TutorNotAvailableException, UserNotFoundException {
        logger.debug("Updating booking: {}", id);
        BookingDetailResponse booking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingDetailResponse> confirmBooking(@PathVariable String id)
        throws BookingNotFoundException, InsufficientBalanceException, UserNotFoundException {
        logger.info("API call: confirm booking {}", id);
        BookingDetailResponse response = bookingService.confirmBooking(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id)
        throws BookingNotFoundException, PaymentNotFoundException, UserNotFoundException {
        logger.debug("Cancelling booking: {}", id);
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable String id)
        throws BookingNotFoundException, UserNotFoundException {
        logger.debug("Completing booking: {}", id);
        bookingService.completeBooking(id);
        return ResponseEntity.ok("Booking completed successfully");
    }
}
