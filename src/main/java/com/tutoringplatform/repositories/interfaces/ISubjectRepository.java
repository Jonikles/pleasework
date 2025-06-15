package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Subject;

public interface ISubjectRepository extends IRepository<Subject> {
    Subject findByName(String name);
    List<Subject> findByCategory(String category);
}