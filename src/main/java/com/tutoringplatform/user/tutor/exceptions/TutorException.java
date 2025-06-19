package com.tutoringplatform.user.tutor.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class TutorException extends TutoringPlatformException {
    protected TutorException(String errorCode, String message) {
        super(errorCode, message);
    }
}
