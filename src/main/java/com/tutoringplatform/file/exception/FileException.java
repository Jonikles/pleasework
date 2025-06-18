package com.tutoringplatform.file.exception;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class FileException extends TutoringPlatformException {
    protected FileException(String errorCode, String message) {
        super(errorCode, message);
    }
}
