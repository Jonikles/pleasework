package com.tutoringplatform.repositories.interfaces;

import com.tutoringplatform.models.User;

public interface IUserRepository<T extends User> extends IRepository<T> {
    T findByEmail(String email);
    boolean emailExists(String email);
}
