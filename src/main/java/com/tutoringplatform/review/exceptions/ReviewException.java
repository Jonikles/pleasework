package com.tutoringplatform.review.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public abstract class ReviewException extends TutoringPlatformException {
    protected ReviewException(String errorCode, String message) {
        super(errorCode, message);
    }
}
