package com.tutoringplatform.user.exceptions;

public class InvalidPasswordException extends ProfileUpdateException {

    private String reason;
    public InvalidPasswordException(String reason) {
        super("password", reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
