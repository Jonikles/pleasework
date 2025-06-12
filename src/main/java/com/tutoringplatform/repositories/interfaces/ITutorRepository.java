package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;

public interface ITutorRepository extends IUserRepository<Tutor> {
    List<Tutor> findBySubject(Subject subject);
    List<Tutor> findByHourlyRateBetween(double min, double max);
    List<Tutor> findByMinimumRating(double rating);
}