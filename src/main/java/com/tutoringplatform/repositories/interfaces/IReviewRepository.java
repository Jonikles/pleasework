package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Review;

public interface IReviewRepository {
    Review findById(String id);
    List<Review> findAll();
    List<Review> getTutorReviews(String tutorId);
    List<Review> getStudentReviews(String studentId);
    Review findByStudentIdAndTutorId(String studentId, String tutorId);
    void save(Review review);
    void update(Review review);
    void delete(String id);
}