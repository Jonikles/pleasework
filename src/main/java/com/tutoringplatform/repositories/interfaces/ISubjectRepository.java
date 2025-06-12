package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Subject;

public interface ISubjectRepository {
    Subject findById(String id);
    Subject findByName(String name);
    List<Subject> findAll();
    List<Subject> findByCategory(String category);
    void save(Subject subject);
    void update(Subject subject);
    void delete(String id);
}