// FILE: src/main/java/com/tutoringplatform/repositories/impl/AvailabilityRepository.java
package com.tutoringplatform.repositories.impl;

import com.tutoringplatform.models.availability.TutorAvailability;
import com.tutoringplatform.repositories.interfaces.IAvailabilityRepository;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AvailabilityRepository implements IAvailabilityRepository {
    private Map<String, TutorAvailability> availabilities = new HashMap<>();

    @Override
    public TutorAvailability findByTutorId(String tutorId) {
        return availabilities.get(tutorId);
    }

    @Override
    public void save(TutorAvailability availability) {
        availabilities.put(availability.getTutorId(), availability);
    }

    @Override
    public void update(TutorAvailability availability) {
        availabilities.put(availability.getTutorId(), availability);
    }

    @Override
    public void delete(String tutorId) {
        availabilities.remove(tutorId);
    }
}