package com.tutoringplatform.user.tutor.exceptions;

public class InvalidHourlyRateException extends TutorException {
    private final double rate;

    public InvalidHourlyRateException(double rate, String reason) {
        super("INVALID_HOURLY_RATE",
                String.format("Invalid hourly rate: $%.2f. %s", rate, reason));
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }
}