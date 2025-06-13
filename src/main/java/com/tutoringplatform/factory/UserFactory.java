// FILE: src/main/java/com/tutoringplatform/factory/UserFactory.java
package com.tutoringplatform.factory;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public enum UserType {
        STUDENT,
        TUTOR
    }

    public User createUser(UserType type, String name, String email, String password, Object... additionalParams) {
        switch (type) {
            case STUDENT:
                return new Student(name, email, password);
            case TUTOR:
                if (additionalParams.length < 2) {
                    throw new IllegalArgumentException("Tutor requires hourlyRate and description");
                }
                double hourlyRate = (Double) additionalParams[0];
                String description = (String) additionalParams[1];
                return new Tutor(name, email, password, hourlyRate, description);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
}