package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Student;

public interface IStudentRepository extends IUserRepository<Student> {
    List<Student> findByNameContaining(String name);
}