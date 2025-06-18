package com.tutoringplatform.subject.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class SubjectException extends TutoringPlatformException {
    protected SubjectException(String errorCode, String message) {
        super(errorCode, message);
    }
}
