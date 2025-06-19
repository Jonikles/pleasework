package com.tutoringplatform.user.exceptions;

public class UserNotFoundException extends UserException {
    private final String userId;
    private final String identifier;

    public UserNotFoundException(String userId) {
        super("USER_NOT_FOUND", String.format("User not found with id: %s", userId));
        this.userId = userId;
        this.identifier = "id";
    }

    public UserNotFoundException(String email, boolean byEmail) {
        super("USER_NOT_FOUND", String.format("User not found with email: %s", email));
        this.userId = email;
        this.identifier = "email";
    }

    public String getUserId() {
        return userId;
    }

    public String getIdentifier() {
        return identifier;
    }
}