package com.tutoringplatform.user.availability;

import com.tutoringplatform.user.availability.model.TutorAvailability;

public interface IAvailabilityRepository {
    TutorAvailability findByTutorId(String tutorId);

    void save(TutorAvailability availability);

    void update(TutorAvailability availability);

    void delete(String tutorId);
}