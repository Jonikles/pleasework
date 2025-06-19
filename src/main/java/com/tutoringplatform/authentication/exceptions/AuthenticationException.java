package com.tutoringplatform.authentication.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class AuthenticationException extends TutoringPlatformException {
    protected AuthenticationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
