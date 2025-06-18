package com.tutoringplatform.services;

import com.tutoringplatform.authentication.IAuthenticationRepository;
import com.tutoringplatform.authentication.AuthenticationService;
import com.tutoringplatform.authentication.exceptions.*;
import com.tutoringplatform.shared.dto.request.SignupRequest;
import com.tutoringplatform.shared.dto.response.AuthResponse;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.shared.factory.UserFactory;
import com.tutoringplatform.shared.util.DTOMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private IAuthenticationRepository authenticationRepository;
    @Mock
    private IStudentRepository studentRepository;
    @Mock
    private ITutorRepository tutorRepository;
    @Mock
    private UserFactory userFactory;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DTOMapper dtoMapper;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                authenticationRepository, studentRepository, tutorRepository,
                userFactory, passwordEncoder, dtoMapper);
    }

    @Test
    void login_Success() throws Exception {
        // Arrange
        String email = "test@email.com";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        Student student = new Student("Test User", email, encodedPassword);
        student.setBalance(100.0);

        AuthResponse expectedResponse = new AuthResponse();

        when(authenticationRepository.findByEmail(email)).thenReturn(student);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(dtoMapper.toAuthResponse(student, 100.0, 0.0)).thenReturn(expectedResponse);

        // Act
        AuthResponse result = authenticationService.login(email, password);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Arrange
        String email = "test@email.com";
        String password = "wrongpassword";

        when(authenticationRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> authenticationService.login(email, password));
    }

    @Test
    void signup_Student_Success() throws Exception {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setUserType("STUDENT");
        request.setName("John Doe");
        request.setEmail("john@email.com");
        request.setPassword("password123");
        request.setTimeZoneId("America/New_York");

        Student student = new Student("John Doe", "john@email.com", "encodedPassword");
        AuthResponse expectedResponse = new AuthResponse();

        when(authenticationRepository.emailExists("john@email.com")).thenReturn(false);
        when(userFactory.createStudent(anyString(), anyString(), anyString())).thenReturn(student);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(dtoMapper.toAuthResponse(any(Student.class), eq(0.0), eq(0.0))).thenReturn(expectedResponse);

        // Act
        AuthResponse result = authenticationService.signup(request);

        // Assert
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void signup_EmailExists_ThrowsException() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setUserType("STUDENT");
        request.setName("John Doe");
        request.setEmail("existing@email.com");
        request.setPassword("password123");

        when(authenticationRepository.emailExists("existing@email.com")).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class,
                () -> authenticationService.signup(request));
    }

    @Test
    void signup_InvalidTutorRegistration_ThrowsException() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setUserType("TUTOR");
        request.setName("Jane Doe");
        request.setEmail("jane@email.com");
        request.setPassword("password123");
        request.setHourlyRate(-10); // Invalid rate

        when(authenticationRepository.emailExists("jane@email.com")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTutorRegistrationException.class,
                () -> authenticationService.signup(request));
    }
}