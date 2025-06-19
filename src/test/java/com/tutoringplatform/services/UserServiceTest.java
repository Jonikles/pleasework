package com.tutoringplatform.services;

import com.tutoringplatform.user.IUserRepository;
import com.tutoringplatform.user.User;
import com.tutoringplatform.user.UserService;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.user.UserType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static class ConcreteUser extends User {
        public ConcreteUser(String id, String name, String email, String password) {
            super(name, email, password, UserType.STUDENT);
            this.setId(id);
        }
    }

    private static class ConcreteUserService extends UserService<ConcreteUser> {
        public ConcreteUserService(IUserRepository<ConcreteUser> repository) {
            super(repository);
        }
    }

    @Mock
    private IUserRepository<ConcreteUser> userRepository;

    private UserService<ConcreteUser> userService;
    private ConcreteUser testUser;

    @BeforeEach
    void setUp() {
        userService = new ConcreteUserService(userRepository);
        testUser = new ConcreteUser("user123", "Test User", "test@example.com", "password");
    }

    @Test
    void findById_withValidId_returnsUser() throws UserNotFoundException {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(testUser);

        // Act
        ConcreteUser foundUser = userService.findById("user123");

        // Assert
        assertNotNull(foundUser);
        assertEquals("user123", foundUser.getId());
        verify(userRepository).findById("user123");
    }

    @Test
    void findById_withNonExistentId_throwsUserNotFoundException() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findById("nonexistent"));
    }

    @Test
    void findById_withNullId_throwsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.findById(null));
    }

    @Test
    void findByEmail_withValidEmail_returnsUser() throws UserNotFoundException {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act
        ConcreteUser foundUser = userService.findByEmail("test@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findAll_whenUsersExist_returnsUserList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        // Act
        List<ConcreteUser> users = userService.findAll();

        // Assert
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void update_whenUserExists_callsRepositoryUpdate() throws UserNotFoundException {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(testUser);

        // Act
        userService.update(testUser);

        // Assert
        verify(userRepository).update(testUser);
    }

    @Test
    void update_whenUserDoesNotExist_throwsUserNotFoundException() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.update(testUser));
        verify(userRepository, never()).update(any());
    }

    @Test
    void delete_whenUserExists_callsRepositoryDelete() throws UserNotFoundException {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(testUser);

        // Act
        userService.delete("user123");

        // Assert
        verify(userRepository).delete("user123");
    }

    @Test
    void delete_whenUserDoesNotExist_throwsUserNotFoundException() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.delete("user123"));
        verify(userRepository, never()).delete(anyString());
    }

    @Test
    void validateUserExists_whenUserExists_completesSuccessfully() throws UserNotFoundException {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(testUser);

        // Act & Assert
        assertDoesNotThrow(() -> userService.validateUserExists("user123"));
        verify(userRepository).findById("user123");
    }

    @Test
    void validateUserExists_whenUserDoesNotExist_throwsUserNotFoundException() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.validateUserExists("nonexistent"));
    }
}