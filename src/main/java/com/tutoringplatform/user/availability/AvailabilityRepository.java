package com.tutoringplatform.user.availability;

import com.tutoringplatform.user.availability.model.TutorAvailability;

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