package com.tutoringplatform.review.exceptions;

public class InvalidRatingException extends ReviewException {
    private final int rating;

    public InvalidRatingException(int rating) {
        super("INVALID_RATING", String.format("Rating %d is invalid", rating));
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }
}
