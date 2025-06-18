package com.tutoringplatform.user.student.exceptions;

public class InvalidFundAmountException extends StudentException {
    private final double amount;

    public InvalidFundAmountException(double amount) {
        super("INVALID_FUND_AMOUNT",
                String.format("Invalid fund amount: $%.2f", amount));
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}