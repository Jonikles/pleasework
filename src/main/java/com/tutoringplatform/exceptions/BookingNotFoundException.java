package com.tutoringplatform.exceptions;

public class BookingNotFoundException extends Exception {
    private final String bookingId;

    public BookingNotFoundException(String bookingId) {
        super("Booking not found: " + bookingId);
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }
}
