package com.tutoringplatform.repositories.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.tutoringplatform.models.Review;
import com.tutoringplatform.repositories.interfaces.IReviewRepository;

@Repository
public class ReviewRepository implements IReviewRepository {
    private Map<String, Review> reviews = new HashMap<>();

    @Override
    public Review findById(String id) {
        return reviews.get(id);
    }

    @Override
    public List<Review> findAll() {
        return new ArrayList<>(reviews.values());
    }

    @Override
    public List<Review> getTutorReviews(String tutorId) {
        return reviews.values().stream()
                .filter(r -> r.getTutorId().equals(tutorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getStudentReviews(String studentId) {
        return reviews.values().stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public Review findByStudentIdAndTutorId(String studentId, String tutorId) {
        return reviews.values().stream()
                .filter(r -> r.getStudentId().equals(studentId) && r.getTutorId().equals(tutorId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(Review review) {
        reviews.put(review.getId(), review);
    }

    @Override
    public void update(Review review) {
        reviews.put(review.getId(), review);
    }

    @Override
    public void delete(String id) {
        reviews.remove(id);
    }
}