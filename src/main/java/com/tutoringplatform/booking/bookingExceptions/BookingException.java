package com.tutoringplatform.booking.bookingExceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class BookingException extends TutoringPlatformException {
    protected BookingException(String errorCode, String message) {
        super(errorCode, message);
    }
}
