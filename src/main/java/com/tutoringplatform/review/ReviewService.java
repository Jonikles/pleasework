package com.tutoringplatform.review;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.user.student.StudentService;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.review.exceptions.*;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.shared.dto.request.CreateReviewRequest;
import com.tutoringplatform.shared.dto.response.ReviewResponse;
import com.tutoringplatform.shared.util.DTOMapper;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class ReviewService {

    private final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final IReviewRepository reviewRepository;
    private final StudentService studentService;
    private final TutorService tutorService;
    private final BookingService bookingService;
    private final DTOMapper dtoMapper;

    @Autowired
    public ReviewService(
            IReviewRepository reviewRepository,
            StudentService studentService,
            TutorService tutorService,
            BookingService bookingService,
            DTOMapper dtoMapper) {
        this.reviewRepository = reviewRepository;
        this.studentService = studentService;
        this.tutorService = tutorService;
        this.bookingService = bookingService;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) throws NoCompletedBookingsException, InvalidRatingException, UserNotFoundException {
        logger.info("Creating review for tutor {} by student {}", request.getTutorId(), request.getStudentId());

        validateCreateReviewRequest(request);
        // Extract student and tutor IDs from request
        String studentId = request.getStudentId();
        String tutorId = request.getTutorId();

        Student student = studentService.findById(studentId);
        Tutor tutor = tutorService.findById(tutorId);

        // Check if student has completed any bookings with this tutor
        List<Booking> completedBookings = bookingService.hasStudentCompletedBookingWithTutor(studentId, tutorId, Booking.BookingStatus.COMPLETED);

        if (completedBookings.isEmpty()) {
            logger.warn("No completed bookings found for student {} with tutor {}", studentId, tutorId);
            throw new NoCompletedBookingsException(studentId, tutorId);
        }

        // Check if review already exists from this student for this tutor
        Review existingReview = reviewRepository.findByStudentIdAndTutorId(studentId, tutorId);

        if (existingReview != null) {
            // Update existing review
            existingReview.setRating(request.getRating());
            existingReview.setComment(request.getComment());
            existingReview.setTimestamp(LocalDateTime.now());
            reviewRepository.update(existingReview);

            return dtoMapper.toReviewResponse(existingReview, student, tutor);
        }

        // Create new review
        Review review = new Review(
                studentId,
                tutorId,
                request.getRating(),
                request.getComment());

        reviewRepository.save(review);

        logger.info("Review created successfully for tutor {} by student {}", tutorId, studentId);
        return dtoMapper.toReviewResponse(review, student, tutor);
    }

    @Transactional
    public void deleteReview(String id) throws ReviewNotFoundException {
        logger.debug("Deleting review with id {}", id);
        Review review = reviewRepository.findById(id);
        if (review == null) {
            logger.error("Review not found with id {}", id);
            throw new ReviewNotFoundException(id);
        }
        
        reviewRepository.delete(id);

        logger.info("Review deleted successfully with id {}", id);
    }

    public List<Review> getTutorReviews(String tutorId) throws NoCompletedBookingsException, UserNotFoundException {
        logger.info("Getting reviews for tutor {}", tutorId);
        tutorService.validateUserExists(tutorId);

        List<Review> reviews = reviewRepository.getTutorReviews(tutorId);

        logger.info("Reviews for tutor {} retrieved successfully", tutorId);
        return reviews;
    }

    public List<ReviewResponse> getTutorReviewsResponse(String tutorId) throws NoCompletedBookingsException, UserNotFoundException {
        logger.info("Getting review DTOs for tutor {}", tutorId);
        List<Review> reviews = getTutorReviews(tutorId);
        
        List<ReviewResponse> responses = new ArrayList<>();

        for (Review review : reviews) {
            Student student = studentService.findById(review.getStudentId());
            Tutor tutor = tutorService.findById(review.getTutorId());

            ReviewResponse response = dtoMapper.toReviewResponse(review, student, tutor);
            responses.add(response);
        }

        // Sort by most recent first
        responses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return responses;
    }

    public List<ReviewResponse> getStudentReviews(String studentId) throws NoCompletedBookingsException, UserNotFoundException {
        logger.info("Getting reviews for student {}", studentId);
        Student student = studentService.findById(studentId);
        List<Review> reviews = reviewRepository.getStudentReviews(studentId);
        List<ReviewResponse> responses = new ArrayList<>();
        for (Review review : reviews) {
            Tutor tutor = tutorService.findById(review.getTutorId());
            ReviewResponse response = dtoMapper.toReviewResponse(review, student, tutor);
            responses.add(response);
        }

        responses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        logger.info("Reviews for student {} retrieved successfully", studentId);
        return responses;
    }

    private void validateCreateReviewRequest(CreateReviewRequest request) throws InvalidRatingException {
        if (request == null) {
            throw new IllegalArgumentException("Review request cannot be null");
        }
        if (request.getRating() < 1 || request.getRating() > 5) {
            logger.error("Invalid rating when creating review for tutor {} by student {}: {}", request.getTutorId(), request.getStudentId(), request.getRating());
            throw new InvalidRatingException(request.getRating());
        }
        if (request.getComment().trim().length() < 50) {
            throw new IllegalArgumentException("Comment must be at least 50 characters");
        }
        if (request.getComment().length() > 1000) {
            throw new IllegalArgumentException("Comment cannot exceed 1000 characters");
        }
    }
}