package com.tutoringplatform.services;

import java.util.List;

import com.tutoringplatform.models.User;
import com.tutoringplatform.repositories.interfaces.IUserRepository;

public abstract class UserService<T extends User> {
    protected IUserRepository<T> repository;

    public UserService(IUserRepository<T> repository) {
        this.repository = repository;
    }

    public T findById(String id) throws Exception {
        T user = repository.findById(id);
        if (user == null) {
            throw new Exception("User not found with id: " + id);
        }
        return user;
    }

    public T findByEmail(String email) throws Exception {
        T user = repository.findByEmail(email);
        if (user == null) {
            throw new Exception("User not found with email: " + email);
        }
        return user;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public void update(T user) throws Exception {
        if (repository.findById(user.getId()) == null) {
            throw new Exception("User not found");
        }
        repository.update(user);
    }

    public void delete(String id) throws Exception {
        if (repository.findById(id) == null) {
            throw new Exception("User not found");
        }
        repository.delete(id);
    }
}