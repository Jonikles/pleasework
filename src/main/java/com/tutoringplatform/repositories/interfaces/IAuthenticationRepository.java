
package com.tutoringplatform.repositories.interfaces;

import com.tutoringplatform.models.User;

public interface IAuthenticationRepository {
    User findByEmail(String email);

    void saveUser(User user);

    boolean emailExists(String email);
}