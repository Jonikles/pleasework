package com.tutoringplatform.shared.exceptions;

import com.tutoringplatform.authentication.exceptions.AuthenticationException;
import com.tutoringplatform.booking.exceptions.BookingException;
import com.tutoringplatform.payment.exceptions.PaymentException;
import com.tutoringplatform.review.exceptions.ReviewException;
import com.tutoringplatform.subject.exceptions.SubjectException;
import com.tutoringplatform.shared.dto.response.ErrorResponse;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ========== TUTORING PLATFORM EXCEPTIONS ==========

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthException(AuthenticationException e) {
            HttpStatus status = determineAuthStatus(e);

            ErrorResponse error = new ErrorResponse(
                    e.getErrorCode(),
                    e.getMessage());

            return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(ReviewException.class)
        public ResponseEntity<ErrorResponse> handleReviewException(ReviewException e) {
            HttpStatus status = determineReviewStatus(e);

            ErrorResponse error = new ErrorResponse(
                    e.getErrorCode(),
                    e.getMessage());

            return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(BookingException.class)
        public ResponseEntity<ErrorResponse> handleBookingException(BookingException e) {
            HttpStatus status = determineBookingStatus(e);

            ErrorResponse error = new ErrorResponse(
                    e.getErrorCode(),
                    e.getMessage());

            return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(PaymentException.class)
        public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
            HttpStatus status = determinePaymentStatus(e);

            ErrorResponse error = new ErrorResponse(
                    e.getErrorCode(),
                    e.getMessage());

            return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(SubjectException.class)
        public ResponseEntity<ErrorResponse> handleSubjectException(SubjectException e) {
            HttpStatus status = determineSubjectStatus(e);

            ErrorResponse error = new ErrorResponse(
                    e.getErrorCode(),
                    e.getMessage());

            return ResponseEntity.status(status).body(error);
        }


    // ========== ILLEGAL EXCEPTIONS ==========

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        // This indicates a programming error - should have been caught by validation
        logger.error("IllegalArgumentException - programming error: {}", e.getMessage(), e);

        ErrorResponse error = new ErrorResponse(
                "INVALID_REQUEST",
                "Invalid request parameters"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        // This indicates data corruption or programming error
        logger.error("IllegalStateException - system error: {}", e.getMessage(), e);

        ErrorResponse error = new ErrorResponse(
                "SYSTEM_ERROR",
                "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ========== GENERIC HANDLERS ==========

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        // This is bad - unexpected exception
        logger.error("Unexpected error", e);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ========== DETERMINE EXCEPTION STATUS ==========

    private HttpStatus determineAuthStatus(AuthenticationException e) {
        switch (e.getErrorCode()) {
            case "INVALID_CREDENTIALS":
                return HttpStatus.UNAUTHORIZED;
            case "EMAIL_EXISTS":
                return HttpStatus.CONFLICT;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }

    private HttpStatus determineReviewStatus(ReviewException e) {
        switch (e.getErrorCode()) {
            case "NO_COMPLETED_BOOKINGS":
                return HttpStatus.FORBIDDEN;
            case "INVALID_RATING":
                return HttpStatus.BAD_REQUEST;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }

    private HttpStatus determineBookingStatus(BookingException e) {
        switch (e.getErrorCode()) {
            case "BOOKING_NOT_FOUND":
                return HttpStatus.NOT_FOUND;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }

    private HttpStatus determinePaymentStatus(PaymentException e) {
        switch (e.getErrorCode()) {
            case "PAYMENT_NOT_FOUND":
                return HttpStatus.NOT_FOUND;
            case "INSUFFICIENT_BALANCE":
                return HttpStatus.BAD_REQUEST;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }

    private HttpStatus determineSubjectStatus(SubjectException e) {
        switch (e.getErrorCode()) {
            case "SUBJECT_NOT_FOUND":
                return HttpStatus.NOT_FOUND;
            case "SUBJECT_EXISTS":
                return HttpStatus.CONFLICT;
            case "ASSIGNED_SUBJECT":
                return HttpStatus.CONFLICT;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }
}