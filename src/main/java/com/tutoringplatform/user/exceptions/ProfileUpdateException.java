package com.tutoringplatform.user.exceptions;

public class ProfileUpdateException extends UserException {
    private final String field;

    public ProfileUpdateException(String field, String reason) {
        super("PROFILE_UPDATE_ERROR",
                String.format("Cannot update %s: %s", field, reason));
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
