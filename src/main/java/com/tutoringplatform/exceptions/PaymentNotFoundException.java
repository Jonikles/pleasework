package com.tutoringplatform.exceptions;

public class PaymentNotFoundException extends Exception {
    private final String paymentId;
    public PaymentNotFoundException(String paymentId) {
        super("Payment not found: " + paymentId);
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
