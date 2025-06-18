package com.tutoringplatform.payment.paymentExceptions;

public class PaymentNotFoundException extends PaymentException {
    private final String paymentId;
    public PaymentNotFoundException(String paymentId) {
        super("PAYMENT_NOT_FOUND", "Payment not found: " + paymentId);
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
