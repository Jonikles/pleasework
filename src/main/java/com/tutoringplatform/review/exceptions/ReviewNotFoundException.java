package com.tutoringplatform.review.exceptions;

public class ReviewNotFoundException extends ReviewException {
    private final String id;

    public ReviewNotFoundException(String id) {
        super("REVIEW_NOT_FOUND", String.format("Review with id %s not found", id));
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
