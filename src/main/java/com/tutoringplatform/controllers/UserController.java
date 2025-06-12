package com.tutoringplatform.controllers;


import com.tutoringplatform.models.User;
import com.tutoringplatform.services.UserService;

public abstract class UserController<T extends User> {
    protected UserService<T> userService;

    public UserController(UserService<T> userService) {
        this.userService = userService;
    }

    public T getUser(String id) throws Exception {
        return userService.findById(id);
    }

    public T getUserByEmail(String email) throws Exception {
        return userService.findByEmail(email);
    }

    public void updateUser(T user) throws Exception {
        userService.update(user);
    }

    public void deleteUser(String id) throws Exception {
        userService.delete(id);
    }
}