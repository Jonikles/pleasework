package com.tutoringplatform.review;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class ReviewService {

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
    public ReviewResponse createReview(CreateReviewRequest request) throws Exception {
        // Extract student and tutor IDs from request
        String studentId = request.getStudentId();
        String tutorId = request.getTutorId();

        // Verify student exists
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new Exception("Student not found");
        }

        // Verify tutor exists
        Tutor tutor = tutorRepository.findById(tutorId);
        if (tutor == null) {
            throw new Exception("Tutor not found");
        }

        // Check if student has completed any bookings with this tutor
        List<Booking> completedBookings = bookingRepository.findByStudentIdAndTutorIdAndStatus(
                studentId, tutorId, Booking.BookingStatus.COMPLETED);

        if (completedBookings.isEmpty()) {
            throw new Exception("Can only review tutors you've had completed sessions with");
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

        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new Exception("Rating must be between 1 and 5");
        }

        // Create new review
        Review review = new Review(
                studentId,
                tutorId,
                request.getRating(),
                request.getComment());

        reviewRepository.save(review);

        // Update tutor's reviews list
        tutor.getReviewsReceived().add(review);
        tutorRepository.update(tutor);

        // Update student's reviews list
        student.getReviewsGiven().add(review);
        studentRepository.update(student);

        return dtoMapper.toReviewResponse(review, student, tutor);
    }

    @Transactional
    public void deleteReview(String id) throws Exception {
        Review review = reviewRepository.findById(id);
        if (review == null) {
            throw new Exception("Review not found");
        }
        Tutor tutor = tutorRepository.findById(review.getTutorId());
        tutor.getReviewsReceived().remove(review);
        tutorRepository.update(tutor);

        Student student = studentRepository.findById(review.getStudentId());
        student.getReviewsGiven().remove(review);
        studentRepository.update(student);
        
        reviewRepository.delete(id);
    }

    public List<ReviewResponse> getTutorReviews(String tutorId) throws Exception {
        Tutor tutor = tutorRepository.findById(tutorId);
        if (tutor == null) {
            throw new Exception("Tutor not found");
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

        return responses;
    }

    public List<ReviewResponse> getStudentReviews(String studentId) throws Exception {
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new Exception("Student not found");
        }
        List<Review> reviews = reviewRepository.getStudentReviews(studentId);
        List<ReviewResponse> responses = new ArrayList<>();
        for (Review review : reviews) {
            Tutor tutor = tutorRepository.findById(review.getTutorId());
            ReviewResponse response = dtoMapper.toReviewResponse(review, student, tutor);
            responses.add(response);
        }

        responses.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return responses;
    }
}