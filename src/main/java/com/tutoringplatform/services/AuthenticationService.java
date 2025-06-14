// FILE: src/main/java/com/tutoringplatform/services/AuthService.java
package com.tutoringplatform.services;

import java.time.ZoneId;
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
    private IAuthenticationRepository authenticationRepository;

    @Autowired
    private UserFactory userFactory;

    public User login(String email, String password) throws Exception {
        User user = authenticationRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid email or password");
        }
        return user;
    }

    public Student signupStudent(String name, String email, String password, String timeZoneId) throws Exception {
        validateSignup(email);

        Student student = userFactory.createStudent(name, email, password);
        
        if (timeZoneId != null && !timeZoneId.isEmpty()) {
            try {
                ZoneId zone = ZoneId.of(timeZoneId);
                student.setTimeZone(zone);
            } catch (Exception e) {
                // Invalid timezone, use default
            }
        }
        authenticationRepository.saveUser(student);
        return student;
    }

    public Tutor signupTutor(String name, String email, String password,
            double hourlyRate, String description, String timeZoneId) throws Exception {
        validateSignup(email);

        if (hourlyRate <= 0) {
            throw new Exception("Hourly rate must be positive");
        }

        Tutor tutor = userFactory.createTutor(name, email, password, hourlyRate, description);

    

        authenticationRepository.saveUser(tutor);
        return tutor;
    }

    private void validateSignup(String email) throws Exception {
        if (authenticationRepository.emailExists(email)) {
            throw new Exception("Email already exists");
        }
    }
}