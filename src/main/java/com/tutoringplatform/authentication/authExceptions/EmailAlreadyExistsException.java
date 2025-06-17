package com.tutoringplatform.authentication.authExceptions;

public class EmailAlreadyExistsException extends AuthenticationException {
    private final String email;

    public EmailAlreadyExistsException(String email) {
        super("EMAIL_EXISTS", String.format("Email %s already exists", email));
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
