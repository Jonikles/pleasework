package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Review;

public interface IReviewRepository {
    Review findById(String id);
    List<Review> findAll();
    List<Review> findByTutorId(String tutorId);
    List<Review> findByStudentId(String studentId);
    Review findByBookingId(String bookingId);
    void save(Review review);
    void update(Review review);
    void delete(String id);
}