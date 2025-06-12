package com.tutoringplatform.services;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.User;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService {
    @Autowired
    private IStudentRepository studentRepository;
    @Autowired
    private ITutorRepository tutorRepository;


    public User login(String email, String password) throws Exception {
        Student student = studentRepository.findByEmail(email);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }

        Tutor tutor = tutorRepository.findByEmail(email);
        if (tutor != null && tutor.getPassword().equals(password)) {
            return tutor;
        }

        throw new Exception("Invalid email or password");
    }

    public Student signupStudent(String name, String email, String password) throws Exception {
        if (studentRepository.findByEmail(email) != null || tutorRepository.findByEmail(email) != null) {
            throw new Exception("Email already exists");
        }

        Student student = new Student(name, email, password);
        studentRepository.save(student);
        return student;
    }

    public Tutor signupTutor(String name, String email, String password,
            double hourlyRate, String description) throws Exception {
        if (studentRepository.findByEmail(email) != null || tutorRepository.findByEmail(email) != null) {
            throw new Exception("Email already exists");
        }

        if (hourlyRate <= 0) {
            throw new Exception("Hourly rate must be positive");
        }

        Tutor tutor = new Tutor(name, email, password, hourlyRate, description);
        tutorRepository.save(tutor);
        return tutor;
    }
}