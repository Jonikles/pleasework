package com.tutoringplatform.review.reviewExceptions;

public class ReviewNotFoundException extends ReviewException {
    public ReviewNotFoundException(String id) {
        super("REVIEW_NOT_FOUND", String.format("Review with id %s not found", id));
    }
}
