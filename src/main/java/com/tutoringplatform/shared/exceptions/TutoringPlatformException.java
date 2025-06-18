package com.tutoringplatform.shared.exceptions;

public abstract class TutoringPlatformException extends Exception {
    private final String errorCode; 

    protected TutoringPlatformException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected TutoringPlatformException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
