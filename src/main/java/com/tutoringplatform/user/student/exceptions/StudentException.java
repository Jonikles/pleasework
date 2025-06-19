package com.tutoringplatform.user.student.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class StudentException extends TutoringPlatformException {
    protected StudentException(String errorCode, String message) {
        super(errorCode, message);
    }
}
