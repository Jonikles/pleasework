package com.tutoringplatform.user.tutor;

import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.IUserRepository;

import java.util.List;

public interface ITutorRepository extends IUserRepository<Tutor>{
    List<Tutor> findBySubject(Subject subject); 
}