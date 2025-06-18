package com.tutoringplatform.payment.paymentExceptions;

import com.tutoringplatform.exceptions.TutoringPlatformException;

public abstract class PaymentException extends TutoringPlatformException {
    protected PaymentException(String errorCode, String message) {
        super(errorCode, message);
    }
}
