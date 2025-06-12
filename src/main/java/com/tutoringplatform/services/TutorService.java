package com.tutoringplatform.services;

import java.util.List;
import java.util.stream.Collectors;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TutorService extends UserService<Tutor> {
    @Autowired
    public TutorService(ITutorRepository repository) {
        super(repository);
    }

    public void register(Tutor tutor) throws Exception {
        if (repository.findByEmail(tutor.getEmail()) != null) {
            throw new Exception("Email already exists");
        }
        if (tutor.getHourlyRate() <= 0) {
            throw new Exception("Hourly rate must be positive");
        }
        repository.save(tutor);
    }

    public List<Tutor> searchTutors(Subject subject, Double minPrice, Double maxPrice,
            Double minRating, String day, Integer hour) {
        List<Tutor> tutors = repository.findAll();

        if (subject != null) {
            tutors = tutors.stream()
                    .filter(t -> t.getSubjects().contains(subject))
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            tutors = tutors.stream()
                    .filter(t -> t.getHourlyRate() >= minPrice)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            tutors = tutors.stream()
                    .filter(t -> t.getHourlyRate() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (minRating != null) {
            tutors = tutors.stream()
                    .filter(t -> t.getAverageRating() >= minRating)
                    .collect(Collectors.toList());
        }

        if (day != null && hour != null) {
            tutors = tutors.stream()
                    .filter(t -> t.isAvailable(day, hour))
                    .collect(Collectors.toList());
        }

        return tutors;
    }

    public void updateAvailability(String tutorId, String day, int hour, boolean add) throws Exception {
        Tutor tutor = findById(tutorId);
        if (add) {
            tutor.addAvailability(day, hour);
        } else {
            tutor.removeAvailability(day, hour);
        }
        repository.update(tutor);
    }
}