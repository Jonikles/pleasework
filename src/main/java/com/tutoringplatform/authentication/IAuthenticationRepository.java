package com.tutoringplatform.security.authentication;

import com.tutoringplatform.user.User;

public interface IAuthenticationRepository {
    User findByEmail(String email);

    void saveUser(User user);

    boolean emailExists(String email);
}