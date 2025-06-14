package com.tutoringplatform.repositories.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;

@Repository
public class StudentRepository implements IStudentRepository {
    private Map<String, Student> students = new HashMap<>();

    @Override
    public Student findById(String id) {
        return students.get(id);
    }

    @Override
    public Student findByEmail(String email) {
        return students.values().stream()
                .filter(s -> s.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    @Override
    public void save(Student student) {
        students.put(student.getId(), student);
    }

    @Override
    public void update(Student student) {
        students.put(student.getId(), student);
    }

    @Override
    public void delete(String id) {
        students.remove(id);
    }

    @Override
    public List<Student> findByNameContaining(String name) {
        return students.values().stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}