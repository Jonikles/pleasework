
package com.tutoringplatform.repositories.impl;

import com.tutoringplatform.models.User;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.IAuthRepository;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRepository implements IAuthRepository {

    @Autowired
    private IStudentRepository studentRepository;

    @Autowired
    private ITutorRepository tutorRepository;

    @Override
    public User findByEmail(String email) {
        Student student = studentRepository.findByEmail(email);
        if (student != null)
            return student;

        return tutorRepository.findByEmail(email);
    }

    @Override
    public void saveUser(User user) {
        if (user instanceof Student) {
            studentRepository.save((Student) user);
        } else if (user instanceof Tutor) {
            tutorRepository.save((Tutor) user);
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
    }

    @Override
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }
}