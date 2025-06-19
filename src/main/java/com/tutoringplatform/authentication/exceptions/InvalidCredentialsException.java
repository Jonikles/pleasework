package com.tutoringplatform.authentication.exceptions;

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid email or password");
    }
}
