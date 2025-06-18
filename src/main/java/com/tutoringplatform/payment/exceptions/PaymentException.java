package com.tutoringplatform.payment.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class PaymentException extends TutoringPlatformException {
    protected PaymentException(String errorCode, String message) {
        super(errorCode, message);
    }
}
