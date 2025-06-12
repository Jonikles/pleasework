package com.tutoringplatform.services;

import java.util.List;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.repositories.interfaces.ISubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SubjectService {
    @Autowired
    private ISubjectRepository subjectRepository;

    public Subject createSubject(String name, String category) throws Exception {
        if (subjectRepository.findByName(name) != null) {
            throw new Exception("Subject already exists");
        }

        Subject subject = new Subject(name, category);
        subjectRepository.save(subject);
        return subject;
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