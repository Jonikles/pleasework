package com.tutoringplatform.services;

import java.util.List;

import com.tutoringplatform.models.Booking;
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
    @Autowired
    private IReviewRepository reviewRepository;
    @Autowired
    private IBookingRepository bookingRepository;
    @Autowired
    private ITutorRepository tutorRepository;
    @Autowired
    private IStudentRepository studentRepository;

    public Review createReview(String bookingId, int rating, String comment) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }
        validateRating(rating);

        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            throw new Exception("Can only review completed bookings");
        }

        if (reviewRepository.findByBookingId(bookingId) != null) {
            throw new Exception("Review already exists for this booking");
        }

        Review review = new Review(booking.getStudentId(), booking.getTutorId(), bookingId, rating, comment);
        reviewRepository.save(review);

        Tutor tutor = tutorRepository.findById(booking.getTutorId());
        if (tutor != null) {
            tutor.addReview(review);
            tutorRepository.update(tutor);
        } else {
            System.err.println("Tutor with ID " + booking.getTutorId() + " not found during review creation.");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        if (student != null) {
            student.addReview(review);
            studentRepository.update(student);
        } else {
            System.err.println("Student with ID " + booking.getStudentId() + " not found during review creation.");
        }

        return review;
    }

    public List<Review> findByTutor(String tutorId) {
        return reviewRepository.findByTutorId(tutorId);
    }

    public List<Review> findByStudent(String studentId) {
        return reviewRepository.findByStudentId(studentId);
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}