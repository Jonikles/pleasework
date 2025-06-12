package com.tutoringplatform.services;

import java.util.List;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class StudentService extends UserService<Student> {

    @Autowired
    public StudentService(IStudentRepository repository) {
        super(repository);
    }

    public void register(Student student) throws Exception {
        if (repository.findByEmail(student.getEmail()) != null) {
            throw new Exception("Email already exists");
        }
        repository.save(student);
    }

    public void addFunds(String studentId, double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Amount must be positive");
        }
        Student student = findById(studentId);
        student.addFunds(amount);
        repository.update(student);
    }

    public List<Student> searchByName(String name) {
        return ((IStudentRepository) repository).findByNameContaining(name);
    }
}