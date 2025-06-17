package com.tutoringplatform.review.reviewExceptions;

public class InvalidRatingException extends ReviewException {
    private final int rating;

    public InvalidRatingException(String message, int rating) {
        super("INVALID_RATING", message);
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }
}
