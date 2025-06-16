package com.tutoringplatform.user.student;

import java.util.List;

import com.tutoringplatform.user.IUserRepository;

public interface IStudentRepository extends IUserRepository<Student> {
    List<Student> findByNameContaining(String name);
}