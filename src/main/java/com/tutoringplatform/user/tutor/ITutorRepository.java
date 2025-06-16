package com.tutoringplatform.user.tutor;

import java.util.List;

import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.IUserRepository;

public interface ITutorRepository extends IUserRepository<Tutor>{
    List<Tutor> findBySubject(Subject subject); 
}