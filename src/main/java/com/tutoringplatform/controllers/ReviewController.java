package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.CreateReviewRequest;
import com.tutoringplatform.dto.response.ReviewResponse;
import com.tutoringplatform.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest request) {
        try {
            ReviewResponse review = reviewService.createReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorReviews(@PathVariable String tutorId) {
        try {
            List<ReviewResponse> reviews = reviewService.getTutorReviewsWithDetails(tutorId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}