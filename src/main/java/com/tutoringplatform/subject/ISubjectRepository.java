package com.tutoringplatform.subject;

import java.util.List;

import com.tutoringplatform.shared.IRepository;

public interface ISubjectRepository extends IRepository<Subject> {
    Subject findByName(String name);
    List<Subject> findByCategory(String category);
}