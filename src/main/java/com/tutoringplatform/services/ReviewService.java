package com.tutoringplatform.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tutoringplatform.models.Review;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.IBookingRepository;
import com.tutoringplatform.repositories.interfaces.IReviewRepository;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ReviewService {
    private final IReviewRepository reviewRepository;
    private final IBookingRepository bookingRepository;
    private final ITutorRepository tutorRepository;
    private final IStudentRepository studentRepository;
    private final StudentService studentService;
    private final TutorService tutorService;

    @Autowired
    public ReviewService(IReviewRepository reviewRepository, IBookingRepository bookingRepository,
            ITutorRepository tutorRepository, IStudentRepository studentRepository, StudentService studentService,
            TutorService tutorService) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.tutorRepository = tutorRepository;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
        this.tutorService = tutorService;
    }

    public Review createOrUpdateReview(String studentId, String tutorId, int rating, String comment) throws Exception {
        Student student = studentService.findById(studentId);
        Tutor tutor = tutorService.findById(tutorId);

        // Check if student has completed any bookings with this tutor
        if (!bookingRepository.hasCompletedBooking(studentId, tutorId)) {
            throw new Exception("Can only review tutors you've had completed sessions with");
        }

        // Check if review already exists
        Review existingReview = reviewRepository.findByStudentIdAndTutorId(studentId, tutorId);

        if (existingReview != null) {
            // Update existing review
            existingReview.setRating(rating);
            existingReview.setComment(comment);
            existingReview.setTimestamp(LocalDateTime.now());
            reviewRepository.update(existingReview);
            return existingReview;
        } else {
            // Create new review
            Review review = new Review(studentId, tutorId, rating, comment);
            reviewRepository.save(review);

            // Update tutor's reviews list
            tutor.getReviewsReceived().add(review);
            tutorRepository.update(tutor);

            // Update student's reviews list
            student.getReviewsGiven().add(review);
            studentRepository.update(student);

            return review;
        }
    }

    public List<Review> getTutorReviews(String tutorId) {
        return reviewRepository.getTutorReviews(tutorId);
    }

    public List<Review> getStudentReviews(String studentId) {
        return reviewRepository.getStudentReviews(studentId);
    }
}