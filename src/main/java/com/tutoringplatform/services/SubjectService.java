package com.tutoringplatform.services;

import java.util.List;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.ISubjectRepository;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import com.tutoringplatform.dto.response.SubjectResponse;
import com.tutoringplatform.util.DTOMapper;

@Service
public class SubjectService {
    private final ISubjectRepository subjectRepository;
    private final ITutorRepository tutorRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public SubjectService(ISubjectRepository subjectRepository, ITutorRepository tutorRepository, DTOMapper dtoMapper) {
        this.subjectRepository = subjectRepository;
        this.tutorRepository = tutorRepository;
        this.dtoMapper = dtoMapper;
    }

    public Subject createSubject(String name, String category) throws Exception {
        if (subjectRepository.findByName(name) != null) {
            throw new Exception("Subject already exists");
        }

        Subject subject = new Subject(name, category);
        subjectRepository.save(subject);
        return subject;
    }

    public List<SubjectResponse> getAvailableSubjectsForTutor(String tutorId) throws Exception {
        Tutor tutor = tutorRepository.findById(tutorId);
        List<Subject> subjects = subjectRepository.findAll();
        List<Subject> availableSubjects = new ArrayList<>();
        for (Subject subject : subjects) {
            if (!tutor.getSubjects().contains(subject)) {
                availableSubjects.add(subject);
            }
        }
        List<SubjectResponse> subjectResponses = new ArrayList<>();
        for (Subject subject : availableSubjects) {
            subjectResponses.add(dtoMapper.toSubjectResponse(subject));
        }
        return subjectResponses;
    }

    public Subject findById(String id) throws Exception {
        Subject subject = subjectRepository.findById(id);
        if (subject == null) {
            throw new Exception("Subject not found");
        }
        return subject;
    }

    public Subject findByName(String name) throws Exception {
        Subject subject = subjectRepository.findByName(name);
        if (subject == null) {
            throw new Exception("Subject not found");
        }
        return subject;
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public List<Subject> findByCategory(String category) {
        return subjectRepository.findByCategory(category);
    }
}