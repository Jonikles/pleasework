package com.tutoringplatform.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.tutoringplatform.models.*;
import com.tutoringplatform.repositories.interfaces.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IStudentRepository studentRepository;

    @Mock
    private ITutorRepository tutorRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();

        ReflectionTestUtils.setField(authService, "studentRepository", studentRepository);
        ReflectionTestUtils.setField(authService, "tutorRepository", tutorRepository);
    }

    @Test
    @DisplayName("Should login student successfully")
    void testLoginStudent() throws Exception {
        Student student = new Student("John Doe", "john@student.com", "password123");
        when(studentRepository.findByEmail("john@student.com")).thenReturn(student);

        User loggedInUser = authService.login("john@student.com", "password123");

        assertNotNull(loggedInUser);
        assertEquals("John Doe", loggedInUser.getName());
        assertEquals(User.UserType.STUDENT, loggedInUser.getUserType());
    }

    @Test
    @DisplayName("Should login tutor successfully")
    void testLoginTutor() throws Exception {
        Tutor tutor = new Tutor("Jane Smith", "jane@tutor.com", "password123", 50.0, "Math tutor");
        when(studentRepository.findByEmail("jane@tutor.com")).thenReturn(null);
        when(tutorRepository.findByEmail("jane@tutor.com")).thenReturn(tutor);

        User loggedInUser = authService.login("jane@tutor.com", "password123");

        assertNotNull(loggedInUser);
        assertEquals("Jane Smith", loggedInUser.getName());
        assertEquals(User.UserType.TUTOR, loggedInUser.getUserType());
    }

    @Test
    @DisplayName("Should throw exception for invalid email")
    void testLoginInvalidEmail() {
        when(studentRepository.findByEmail("invalid@email.com")).thenReturn(null);
        when(tutorRepository.findByEmail("invalid@email.com")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            authService.login("invalid@email.com", "password");
        }, "Invalid email or password");
    }

    @Test
    @DisplayName("Should throw exception for invalid password")
    void testLoginInvalidPassword() {
        Student student = new Student("John Doe", "john@student.com", "correctpassword");
        when(studentRepository.findByEmail("john@student.com")).thenReturn(student);

        assertThrows(Exception.class, () -> {
            authService.login("john@student.com", "wrongpassword");
        }, "Invalid email or password");
    }

    @Test
    @DisplayName("Should signup student successfully")
    void testSignupStudent() throws Exception {
        when(studentRepository.findByEmail("new@student.com")).thenReturn(null);
        when(tutorRepository.findByEmail("new@student.com")).thenReturn(null);

        Student newStudent = authService.signupStudent("New Student", "new@student.com", "password123");

        assertNotNull(newStudent);
        assertEquals("New Student", newStudent.getName());
        assertEquals("new@student.com", newStudent.getEmail());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists for student")
    void testSignupStudentEmailExists() {
        Student existingStudent = new Student("Existing", "existing@email.com", "password");
        when(studentRepository.findByEmail("existing@email.com")).thenReturn(existingStudent);

        assertThrows(Exception.class, () -> {
            authService.signupStudent("New Student", "existing@email.com", "password123");
        }, "Email already exists");
    }

    @Test
    @DisplayName("Should signup tutor successfully")
    void testSignupTutor() throws Exception {
        when(studentRepository.findByEmail("new@tutor.com")).thenReturn(null);
        when(tutorRepository.findByEmail("new@tutor.com")).thenReturn(null);

        Tutor newTutor = authService.signupTutor("New Tutor", "new@tutor.com", "password123",
                75.0, "Experienced tutor");

        assertNotNull(newTutor);
        assertEquals("New Tutor", newTutor.getName());
        assertEquals(75.0, newTutor.getHourlyRate());
        verify(tutorRepository).save(any(Tutor.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid hourly rate")
    void testSignupTutorInvalidRate() {
        when(studentRepository.findByEmail("new@tutor.com")).thenReturn(null);
        when(tutorRepository.findByEmail("new@tutor.com")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            authService.signupTutor("New Tutor", "new@tutor.com", "password123",
                    -10.0, "Description");
        }, "Hourly rate must be positive");
    }

    @Test
    @DisplayName("Should check both repositories for existing email")
    void testSignupChecksBothRepositories() throws Exception {
        Tutor existingTutor = new Tutor("Existing", "existing@email.com", "password", 50.0, "Desc");
        when(studentRepository.findByEmail("existing@email.com")).thenReturn(null);
        when(tutorRepository.findByEmail("existing@email.com")).thenReturn(existingTutor);

        assertThrows(Exception.class, () -> {
            authService.signupStudent("New Student", "existing@email.com", "password123");
        }, "Email already exists");
    }
}