// FILE: src/main/java/com/tutoringplatform/services/AuthService.java
package com.tutoringplatform.services;

import com.tutoringplatform.factory.UserFactory;
import com.tutoringplatform.models.User;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.IAuthenticationRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthenticationService {

    @Autowired
    private IAuthenticationRepository authRepository;

    @Autowired
    private UserFactory userFactory;

    public User login(String email, String password) throws Exception {
        User user = authRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid email or password");
        }
        return user;
    }

    public Student signupStudent(String name, String email, String password) throws Exception {
        validateSignup(email);

        User user = userFactory.createStudent(name, email, password);
        authRepository.saveUser(user);
        return (Student) user;
    }

    public Tutor signupTutor(String name, String email, String password,
            double hourlyRate, String description) throws Exception {
        validateSignup(email);

        if (hourlyRate <= 0) {
            throw new Exception("Hourly rate must be positive");
        }

        User user = userFactory.createTutor(name, email, password, hourlyRate, description);
        authRepository.saveUser(user);
        return (Tutor) user;
    }

    private void validateSignup(String email) throws Exception {
        if (authRepository.emailExists(email)) {
            throw new Exception("Email already exists");
        }
    }
}