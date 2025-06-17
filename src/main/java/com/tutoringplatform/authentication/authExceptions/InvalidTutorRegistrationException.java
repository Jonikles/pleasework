package com.tutoringplatform.authentication.authExceptions;

public class InvalidTutorRegistrationException extends AuthenticationException {
    private final String message;

    public InvalidTutorRegistrationException(String message) {
        super("INVALID_TUTOR_REGISTRATION", String.format("Invalid tutor registration: %s", message));
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
