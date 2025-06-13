
package com.tutoringplatform.repositories.interfaces;

import com.tutoringplatform.models.availability.TutorAvailability;

public interface IAvailabilityRepository {
    TutorAvailability findByTutorId(String tutorId);

    void save(TutorAvailability availability);

    void update(TutorAvailability availability);

    void delete(String tutorId);
}