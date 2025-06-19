package com.tutoringplatform.user.student;

import com.tutoringplatform.user.IUserRepository;

import java.util.List;

public interface IStudentRepository extends IUserRepository<Student> {
    List<Student> findByNameContaining(String name);
}