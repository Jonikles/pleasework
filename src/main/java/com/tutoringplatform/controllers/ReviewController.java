// FILE: src/main/java/com/tutoringplatform/controllers/ReviewController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.ReviewRequest;
import com.tutoringplatform.dto.response.ReviewResponse;
import com.tutoringplatform.models.Review;
import com.tutoringplatform.services.ReviewService;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final DTOMapper dtoMapper;

    @Autowired
    public ReviewController(ReviewService reviewService, DTOMapper dtoMapper) {
        this.reviewService = reviewService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.createOrUpdateReview(
                    request.getStudentId(),
                    request.getTutorId(),
                    request.getRating(),
                    request.getComment());
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toReviewResponse(review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorReviews(@PathVariable String tutorId) {
        List<Review> reviews = reviewService.getTutorReviews(tutorId);
        List<ReviewResponse> responses = reviews.stream()
                .map(dtoMapper::toReviewResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentReviews(@PathVariable String studentId) {
        List<Review> reviews = reviewService.getStudentReviews(studentId);
        List<ReviewResponse> responses = reviews.stream()
                .map(dtoMapper::toReviewResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}