package com.tutoringplatform.subject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Qualifier("subjectRepository")
public class SubjectRepository implements ISubjectRepository {
    private Map<String, Subject> subjects = new HashMap<>();

    @Override
    public Subject findById(String id) {
        return subjects.get(id);
    }

    @Override
    public Subject findByName(String name) {
        return subjects.values().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Subject> findAll() {
        return new ArrayList<>(subjects.values());
    }

    @Override
    public List<Subject> findByCategory(String category) {
        return subjects.values().stream()
                .filter(s -> s.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Subject subject) {
        subjects.put(subject.getId(), subject);
    }

    @Override
    public void update(Subject subject) {
        subjects.put(subject.getId(), subject);
    }

    @Override
    public void delete(String id) {
        subjects.remove(id);
    }
}