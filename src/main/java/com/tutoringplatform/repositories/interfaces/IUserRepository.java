package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.User;

public interface IUserRepository<T extends User> {
    T findById(String id);
    T findByEmail(String email);
    List<T> findAll();
    void save(T user);
    void update(T user);
    void delete(String id);
}