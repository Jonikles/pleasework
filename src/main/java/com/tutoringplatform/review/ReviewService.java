package com.tutoringplatform.review;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.review.exceptions.*;
import com.tutoringplatform.shared.dto.request.CreateReviewRequest;
import com.tutoringplatform.shared.dto.response.ReviewResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;

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
    private final IBookingRepository bookingRepository;
    private final IStudentRepository studentRepository;
    private final ITutorRepository tutorRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public ReviewService(
            IReviewRepository reviewRepository,
            IBookingRepository bookingRepository,
            IStudentRepository studentRepository,
            ITutorRepository tutorRepository,
            DTOMapper dtoMapper) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) throws NoCompletedBookingsException, InvalidRatingException {
        logger.info("Creating review for tutor {} by student {}", request.getTutorId(), request.getStudentId());

        validateCreateReviewRequest(request);
        // Extract student and tutor IDs from request
        String studentId = request.getStudentId();
        String tutorId = request.getTutorId();

        // Verify student exists
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            logger.error("Data integrity error: student {} not found when creating review", studentId);
            throw new IllegalStateException("Data corruption: review reference to non-existent student");
        }

        // Verify tutor exists
        Tutor tutor = tutorRepository.findById(tutorId);
        if (tutor == null) {
            logger.error("Data integrity error: tutor {} not found when creating review", tutorId);
            throw new IllegalStateException("Data corruption: review reference to non-existent tutor");
        }

        // Check if student has completed any bookings with this tutor
        List<Booking> completedBookings = bookingRepository.findByStudentIdAndTutorIdAndStatus(
                studentId, tutorId, Booking.BookingStatus.COMPLETED);

        if (completedBookings.isEmpty()) {
            logger.error("No completed bookings found for student {} with tutor {}", studentId, tutorId);
            throw new NoCompletedBookingsException(studentId, tutorId);
        }

        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            logger.error("Invalid rating when creating review for tutor {} by student {}: {}", tutorId, studentId, request.getRating());
            throw new InvalidRatingException(request.getRating());
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

    public List<ReviewResponse> getTutorReviews(String tutorId) throws NoCompletedBookingsException {
        logger.info("Getting reviews for tutor {}", tutorId);
        Tutor tutor = tutorRepository.findById(tutorId);
        if (tutor == null) {
            logger.error("Data integrity error: tutor {} not found when getting reviews", tutorId);
            throw new IllegalStateException("Data corruption: review reference to non-existent tutor");
        }

        List<Review> reviews = reviewRepository.getTutorReviews(tutorId);
        List<ReviewResponse> responses = new ArrayList<>();

        for (Review review : reviews) {
            Student student = studentRepository.findById(review.getStudentId());

            ReviewResponse response = dtoMapper.toReviewResponse(review, student, tutor);
            responses.add(response);
        }

        // Sort by most recent first
        responses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        logger.info("Reviews for tutor {} retrieved successfully", tutorId);
        return responses;
    }

    public List<ReviewResponse> getStudentReviews(String studentId) throws NoCompletedBookingsException {
        logger.info("Getting reviews for student {}", studentId);
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            logger.error("Data integrity error: student {} not found when getting reviews", studentId);
            throw new IllegalStateException("Data corruption: review reference to non-existent student");
        }
        List<Review> reviews = reviewRepository.getStudentReviews(studentId);
        List<ReviewResponse> responses = new ArrayList<>();
        for (Review review : reviews) {
            Tutor tutor = tutorRepository.findById(review.getTutorId());
            ReviewResponse response = dtoMapper.toReviewResponse(review, student, tutor);
            responses.add(response);
        }

        responses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        logger.info("Reviews for student {} retrieved successfully", studentId);
        return responses;
    }

    private void validateCreateReviewRequest(CreateReviewRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Review request cannot be null");
        }
        if (request.getComment().trim().length() < 50) {
            throw new IllegalArgumentException("Comment must be at least 50 characters");
        }
        if (request.getComment().length() > 1000) {
            throw new IllegalArgumentException("Comment cannot exceed 1000 characters");
        }
    }
}