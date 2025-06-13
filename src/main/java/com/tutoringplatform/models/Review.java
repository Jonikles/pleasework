package com.tutoringplatform.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Review {
    private String id;
    private String studentId;
    private String tutorId;
    private String bookingId;
    private int rating;
    private String comment;
    private LocalDateTime timestamp;

    public Review(String studentId, String tutorId, String bookingId, int rating, String comment) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.bookingId = bookingId;
        setRating(rating);
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getTutorId() { return tutorId; }
    public void setTutorId(String tutorId) { this.tutorId = tutorId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public int getRating() { return rating; }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}