package com.tutoringplatform.review;

import java.time.LocalDateTime;
import java.util.UUID;

public class Review {
    private String id;
    private String studentId;
    private String tutorId;
    private int rating;
    private String comment;
    private LocalDateTime timestamp;

    public Review(String studentId, String tutorId, int rating, String comment) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.tutorId = tutorId;
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

    public int getRating() { return rating; }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}