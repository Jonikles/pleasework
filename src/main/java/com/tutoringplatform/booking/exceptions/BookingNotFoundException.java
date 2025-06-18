package com.tutoringplatform.booking.exceptions;

public class BookingNotFoundException extends BookingException {
    private final String bookingId;

    public BookingNotFoundException(String bookingId) {
        super("BOOKING_NOT_FOUND", "Booking not found: " + bookingId);
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }
}
