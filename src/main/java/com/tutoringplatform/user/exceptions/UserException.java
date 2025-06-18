package com.tutoringplatform.user.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class UserException extends TutoringPlatformException {
    protected UserException(String errorCode, String message) {
        super(errorCode, message);
    }
}
