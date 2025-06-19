package com.tutoringplatform.review;

import com.tutoringplatform.shared.IRepository;

import java.util.List;

public interface IReviewRepository extends IRepository<Review> {
    List<Review> getTutorReviews(String tutorId);
    List<Review> getStudentReviews(String studentId);
    Review findByStudentIdAndTutorId(String studentId, String tutorId);
}