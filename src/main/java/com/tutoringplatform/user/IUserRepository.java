package com.tutoringplatform.user;

import com.tutoringplatform.shared.IRepository;

public interface IUserRepository<T extends User> extends IRepository<T> {
    T findByEmail(String email);
    boolean emailExists(String email);
}
