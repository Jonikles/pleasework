package com.tutoringplatform.exceptions;

public class InsufficientBalanceException extends Exception {
    private final String studentId;
    private final double required;
    private final double available;

    public InsufficientBalanceException(String studentId, double required, double available) {
        super("Insufficient balance for student " + studentId + ". Required: " + required + ", Available: " + available);
        this.studentId = studentId;
        this.required = required;
        this.available = available;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getRequired() {
        return required;
    }

    public double getAvailable() {
        return available;
    }
}
