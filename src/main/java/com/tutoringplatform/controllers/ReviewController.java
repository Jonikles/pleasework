package com.tutoringplatform.controllers;

import com.tutoringplatform.models.Review;
import com.tutoringplatform.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.createReview(
                    request.bookingId,
                    request.rating,
                    request.comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorReviews(@PathVariable String tutorId) {
        List<Review> reviews = reviewService.findByTutor(tutorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentReviews(@PathVariable String studentId) {
        List<Review> reviews = reviewService.findByStudent(studentId);
        return ResponseEntity.ok(reviews);
    }

    static class ReviewRequest {
        public String bookingId;
        public int rating;
        public String comment;
    }
}