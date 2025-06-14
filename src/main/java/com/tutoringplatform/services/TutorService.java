package com.tutoringplatform.services;

import java.util.List;

import com.tutoringplatform.models.Review;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import com.tutoringplatform.dto.request.UpdateTutorRequest;
import java.time.ZoneId;

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
        
        repository.update(tutor);
    }


    public void addEarnings(String tutorId, double amount) throws Exception {
        Tutor tutor = findById(tutorId);
        tutor.setEarnings(tutor.getEarnings() + amount);
        repository.update(tutor);
    }

    public double getAverageRating(Tutor tutor) {
        if (tutor.getReviewsReceived().isEmpty())
            return 0.0;
        return tutor.getReviewsReceived().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public double getEarnings(String tutorId) throws Exception {
        Tutor tutor = findById(tutorId);
        return tutor.getEarnings();
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

    public Tutor updateTutor(String tutorId, UpdateTutorRequest request) throws Exception {
        Tutor tutor = findById(tutorId);

        // Only update fields that are provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            tutor.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if email is already taken by another user
            Tutor existing = repository.findByEmail(request.getEmail());
            if (existing != null && !existing.getId().equals(tutorId)) {
                throw new IllegalArgumentException("Email already in use");
            }
            tutor.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            tutor.setPassword(request.getPassword());
        }

        if (request.getTimeZoneId() != null) {
            try {
                ZoneId zone = ZoneId.of(request.getTimeZoneId());
                tutor.setTimeZone(zone);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid timezone");
            }
        }

        if (request.getHourlyRate() != null) {
            if (request.getHourlyRate() <= 0) {
                throw new IllegalArgumentException("Hourly rate must be positive");
            }
            tutor.setHourlyRate(request.getHourlyRate());
        }

        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            tutor.setDescription(request.getDescription());
        }

        repository.update(tutor);
        return tutor;
    }
}