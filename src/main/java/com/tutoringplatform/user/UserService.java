package com.tutoringplatform.user;

import com.tutoringplatform.user.exceptions.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class UserService<T extends User> {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    protected IUserRepository<T> repository;

    public UserService(IUserRepository<T> repository) {
        this.repository = repository;
    }

    public T findById(String id) throws UserNotFoundException {
        logger.debug("Finding user by id: {}", id);

        if (id == null || id.trim().isEmpty()) {
            logger.error("User ID cannot be null or empty");
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        T user = repository.findById(id);
        if (user == null) {
            logger.warn("User not found with id: {}", id);
            throw new UserNotFoundException(id);
        }

        logger.debug("User found with id: {}", id);
        return user;
    }

    public T findByEmail(String email) throws UserNotFoundException {
        logger.debug("Finding user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.error("Email cannot be null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        T user = repository.findByEmail(email);
        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new UserNotFoundException(email, true);
        }
        return user;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public void update(T user) throws UserNotFoundException {
        logger.debug("Updating user: {}", user.getId());

        if (repository.findById(user.getId()) == null) {
            logger.warn("Cannot update - user not found: {}", user.getId());
            throw new UserNotFoundException(user.getId());
        }

        repository.update(user);
        logger.info("User updated successfully: {}", user.getId());
    }

    public void delete(String id) throws UserNotFoundException {
        logger.debug("Deleting user: {}", id);

        if (repository.findById(id) == null) {
            logger.warn("User not found: {}", id);
            throw new UserNotFoundException(id);
        }
        repository.delete(id);
    }

    public void validateUserExists(String userId) throws UserNotFoundException {
        logger.debug("Validating user exists: {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        if (repository.findById(userId) == null) {
            logger.error("User not found with id: {}", userId);
            throw new UserNotFoundException(userId);
        }

        logger.debug("User validation successful: {}", userId);
    }
}