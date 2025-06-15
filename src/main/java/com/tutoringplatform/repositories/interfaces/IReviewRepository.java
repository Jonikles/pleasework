package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Review;

public interface IReviewRepository extends IRepository<Review> {
    List<Review> getTutorReviews(String tutorId);
    List<Review> getStudentReviews(String studentId);
    Review findByStudentIdAndTutorId(String studentId, String tutorId);
}