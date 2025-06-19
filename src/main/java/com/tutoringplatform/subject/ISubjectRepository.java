package com.tutoringplatform.subject;

import com.tutoringplatform.shared.IRepository;

import java.util.List;

public interface ISubjectRepository extends IRepository<Subject> {
    Subject findByName(String name);
    List<Subject> findByCategory(String category);
}