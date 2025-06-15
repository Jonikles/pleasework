package com.tutoringplatform.repositories.interfaces;

import java.util.List;

public interface IRepository<T> {
    T findById(String id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(String id);
}
