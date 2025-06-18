package com.tutoringplatform.review;

import com.tutoringplatform.shared.dto.request.CreateReviewRequest;
import com.tutoringplatform.shared.dto.response.ReviewResponse;
import com.tutoringplatform.review.reviewExceptions.ReviewException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody @Valid CreateReviewRequest request) throws ReviewException {
        logger.debug("Creating review for tutor {} by student {}", request.getTutorId(), request.getStudentId());
        ReviewResponse review = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) throws ReviewException {
        logger.debug("Deleting review with id {}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.OK).body("Review deleted successfully");
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorReviews(@PathVariable String tutorId) throws ReviewException {
        logger.debug("Getting reviews for tutor {}", tutorId);
        List<ReviewResponse> reviews = reviewService.getTutorReviews(tutorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentReviews(@PathVariable String studentId) throws ReviewException {
        logger.debug("Getting reviews for student {}", studentId);
        List<ReviewResponse> reviews = reviewService.getStudentReviews(studentId);
        return ResponseEntity.ok(reviews);
    }
}