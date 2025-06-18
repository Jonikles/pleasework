package com.tutoringplatform.authentication;

import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.User;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.tutor.ITutorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepository implements IAuthenticationRepository {

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