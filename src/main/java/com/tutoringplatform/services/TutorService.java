package com.tutoringplatform.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.tutoringplatform.models.Review;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TutorService extends UserService<Tutor> {
    @Autowired
    private SubjectService subjectService;

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
                    .filter(t -> calculateAverageRating(t) >= minRating)
                    .collect(Collectors.toList());
        }

        if (day != null && hour != null) {
            tutors = tutors.stream()
                    .filter(t -> checkAvailability(t, day, hour))
                    .collect(Collectors.toList());
        }

        return tutors;
    }

    public void updateAvailability(String tutorId, String day, int hour, boolean add) throws Exception {
        Tutor tutor = findById(tutorId);
        if (add) {
            addAvailability(tutorId, day, hour);
        } else {
            removeAvailability(tutorId, day, hour);
        }
        repository.update(tutor);
    }

    public double getAverageRating(String tutorId) throws Exception {
        Tutor tutor = findById(tutorId);
        if (tutor.getReviewsReceived().isEmpty())
            return 0.0;
        return tutor.getReviewsReceived().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public void addAvailability(String tutorId, String day, int hour) throws Exception {
        Tutor tutor = findById(tutorId);
        if (hour >= 0 && hour <= 23 && tutor.getAvailability().containsKey(day)) {
            List<Integer> dayHours = tutor.getAvailability().get(day);
            if (!dayHours.contains(hour)) {
                dayHours.add(hour);
                Collections.sort(dayHours);
            }
        }
        repository.update(tutor);
    }

    public void removeAvailability(String tutorId, String day, int hour) throws Exception {
        Tutor tutor = findById(tutorId);
        if (tutor.getAvailability().containsKey(day)) {
            tutor.getAvailability().get(day).remove(Integer.valueOf(hour));
        }
        repository.update(tutor);
    }

    public boolean isAvailable(String tutorId, String day, int hour) throws Exception {
        Tutor tutor = findById(tutorId);
        return tutor.getAvailability().containsKey(day) &&
                tutor.getAvailability().get(day).contains(hour);
    }

    public void addEarnings(String tutorId, double amount) throws Exception {
        Tutor tutor = findById(tutorId);
        tutor.setEarnings(tutor.getEarnings() + amount);
        repository.update(tutor);
    }

    private double calculateAverageRating(Tutor tutor) {
        if (tutor.getReviewsReceived().isEmpty())
            return 0.0;
        return tutor.getReviewsReceived().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private boolean checkAvailability(Tutor tutor, String day, int hour) {
        return tutor.getAvailability().containsKey(day) &&
                tutor.getAvailability().get(day).contains(hour);
    }


public List<Subject> addSubjectToTutor(String tutorId, String subjectId) throws Exception {
    Tutor tutor = findById(tutorId);
    Subject subject = subjectService.findById(subjectId);
    tutor.addSubject(subject);
    update(tutor);
    return tutor.getSubjects();
}

public List<Subject> removeSubjectFromTutor(String tutorId, String subjectId) throws Exception {
    Tutor tutor = findById(tutorId);
    Subject subject = subjectService.findById(subjectId);
    tutor.removeSubject(subject);
    update(tutor);
    return tutor.getSubjects();
}
}