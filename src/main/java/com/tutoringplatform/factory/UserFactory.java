// FILE: src/main/java/com/tutoringplatform/factory/UserFactory.java
package com.tutoringplatform.factory;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public enum UserType {
        STUDENT,
        TUTOR
    }

    public Student createStudent(String name, String email, String password) {
        return new Student(name, email, password);
    }

    public Tutor createTutor(String name, String email, String password, double hourlyRate, String description) {
        return new Tutor(name, email, password, hourlyRate, description);
    }
}