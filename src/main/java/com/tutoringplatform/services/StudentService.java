package com.tutoringplatform.services;

import java.util.List;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.tutoringplatform.dto.request.UpdateStudentRequest;
import java.time.ZoneId;

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


    public double getBalance(String studentId) throws Exception {
        Student student = findById(studentId);
        return student.getBalance();
    }

    public double addFunds(String studentId, double amount) throws Exception {

        if (amount <= 0) {
            throw new Exception("Amount must be positive");
        }
        Student student = findById(studentId);

        student.setBalance(student.getBalance() + amount);
        repository.update(student);
        return student.getBalance();
    }

    public void deductFunds(String studentId, double amount) throws Exception {
        Student student = findById(studentId);
        if (student.getBalance() < amount) {
            throw new Exception("Insufficient funds");
        }
        student.setBalance(student.getBalance() - amount);
        repository.update(student);
    }

    public List<Student> searchByName(String name) {
        return ((IStudentRepository) repository).findByNameContaining(name);
    }

    public Student updateStudent(String studentId, UpdateStudentRequest request) throws Exception {
        Student student = findById(studentId);

        // Only update fields that are provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            student.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if email is already taken by another user
            Student existing = repository.findByEmail(request.getEmail());
            if (existing != null && !existing.getId().equals(studentId)) {
                throw new IllegalArgumentException("Email already in use");
            }
            student.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            student.setPassword(request.getPassword());
        }

        if (request.getTimeZoneId() != null) {
            try {
                ZoneId zone = ZoneId.of(request.getTimeZoneId());
                student.setTimeZone(zone);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid timezone");
            }
        }

        repository.update(student);
        return student;
    }
}