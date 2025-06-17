package com.tutoringplatform.review;

import java.util.List;

import com.tutoringplatform.shared.IRepository;

public interface IReviewRepository extends IRepository<Review> {
    List<Review> getTutorReviews(String tutorId);
    List<Review> getStudentReviews(String studentId);
    Review findByStudentIdAndTutorId(String studentId, String tutorId);
}