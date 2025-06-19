package com.tutoringplatform.user.student.exceptions;

public class InsufficientBalanceException extends StudentException {
    private final String studentId;
    private final double required;
    private final double available;

    public InsufficientBalanceException(String studentId, double required, double available) {
        super("INSUFFICIENT_BALANCE", "Insufficient balance for student " + studentId + ". Required: " + required + ", Available: " + available);
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
