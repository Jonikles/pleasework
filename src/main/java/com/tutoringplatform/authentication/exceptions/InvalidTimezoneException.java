package com.tutoringplatform.authentication.exceptions;

public class InvalidTimezoneException extends AuthenticationException {
    private final String timezone;

    public InvalidTimezoneException(String timezone) {
        super("INVALID_TIME_ZONE", String.format("Invalid time zone: %s", timezone));
        this.timezone = timezone;
    }

    public String getTimezone() {
        return timezone;
    }
}
