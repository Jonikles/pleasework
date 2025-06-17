package com.tutoringplatform.review.reviewExceptions;

import com.tutoringplatform.exceptions.TutoringPlatformException;

public abstract class ReviewException extends TutoringPlatformException {
    protected ReviewException(String errorCode, String message) {
        super(errorCode, message);
    }
}
