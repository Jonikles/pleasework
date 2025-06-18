package com.tutoringplatform.authentication;

import com.tutoringplatform.authentication.exceptions.*;
import com.tutoringplatform.shared.dto.request.SignupRequest;
import com.tutoringplatform.shared.dto.response.AuthResponse;
import com.tutoringplatform.user.User;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.shared.factory.UserFactory;
import com.tutoringplatform.shared.util.DTOMapper;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.DateTimeException;

@Service
public class AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final IAuthenticationRepository authenticationRepository;
    private final IStudentRepository studentRepository;
    private final ITutorRepository tutorRepository;
    private final UserFactory userFactory;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Autowired
    public AuthenticationService(
            IAuthenticationRepository authenticationRepository,
            IStudentRepository studentRepository,
            ITutorRepository tutorRepository,
            UserFactory userFactory,
            PasswordEncoder passwordEncoder,
            DTOMapper dtoMapper) {
        this.authenticationRepository = authenticationRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.userFactory = userFactory;
        this.passwordEncoder = passwordEncoder;
        this.dtoMapper = dtoMapper;
    }

    public AuthResponse login(String email, String password) throws InvalidCredentialsException {
        logger.info("Logging in user with email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Find user by email
        User user = authenticationRepository.findByEmail(email.toLowerCase().trim());
        if (user == null) {
            logger.warn("Invalid credentials: user not found for email: {}", email);
            throw new InvalidCredentialsException();
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Invalid credentials: password does not match for email: {}", email);
            throw new InvalidCredentialsException();
        }

        logger.info("Login successful for user: {} ", email);

        // Build response based on user type
        double balance = 0;
        double hourlyRate = 0;

        if (user instanceof Student) {
            balance = ((Student) user).getBalance();
        } else if (user instanceof Tutor) {
            hourlyRate = ((Tutor) user).getHourlyRate();
        }

        return dtoMapper.toAuthResponse(user, balance, hourlyRate);
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) throws EmailAlreadyExistsException, InvalidTutorRegistrationException, InvalidTimezoneException {
        logger.info("Signing up user with email: {}", request.getEmail());

        validateSignupRequest(request);

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // Validate email doesn't exist
        if (authenticationRepository.emailExists(normalizedEmail)) {
            logger.warn("Email already exists: {}", normalizedEmail);
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        // Validate timezone
        ZoneId timeZone = parseTimezone(request.getTimeZoneId());

        User user;
        double balance = 0;
        double hourlyRate = 0;

        if ("STUDENT".equalsIgnoreCase(request.getUserType())) {
            user = createStudent(request, normalizedEmail, timeZone);
            balance = ((Student) user).getBalance();
            logger.info("Student created successfully: {}", user.getId());

        } else if ("TUTOR".equalsIgnoreCase(request.getUserType())) {
            validateTutorRegistration(request);
            user = createTutor(request, normalizedEmail, timeZone);
            hourlyRate = ((Tutor) user).getHourlyRate();
            logger.info("Tutor created successfully: {}", user.getId());
        } else {
            logger.warn("Invalid user type: {}", request.getUserType());
            throw new IllegalStateException("Invalid user type passed validation: " + request.getUserType());
        }

        return dtoMapper.toAuthResponse(user, balance, hourlyRate);
    }


    private void validateSignupRequest(SignupRequest request) {
    
        if (request == null) {
            throw new IllegalArgumentException("Signup request cannot be null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (request.getUserType() == null ||
                (!request.getUserType().equalsIgnoreCase("STUDENT") &&
                        !request.getUserType().equalsIgnoreCase("TUTOR"))) {
            throw new IllegalArgumentException("User type must be STUDENT or TUTOR");
        }

        // Basic email format check
        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Password strength check
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    private void validateTutorRegistration(SignupRequest request) throws InvalidTutorRegistrationException {
        if (request.getHourlyRate() <= 0) {
            throw new InvalidTutorRegistrationException("Hourly rate must be positive");
        }

        if (request.getHourlyRate() > 1000) {
            throw new InvalidTutorRegistrationException("Hourly rate cannot exceed $1000");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new InvalidTutorRegistrationException("Description is required for tutors");
        }

        if (request.getDescription().trim().length() < 50) {
            throw new InvalidTutorRegistrationException("Description must be at least 50 characters");
        }

        if (request.getDescription().length() > 1000) {
            throw new InvalidTutorRegistrationException("Description cannot exceed 1000 characters");
        }
    }

    private User createStudent(SignupRequest request, String normalizedEmail, ZoneId timeZone) {
        Student student = userFactory.createStudent(
            request.getName(),
            normalizedEmail,
            passwordEncoder.encode(request.getPassword()));
        student.setTimeZone(timeZone);
        studentRepository.save(student);

        logger.debug("Student created successfully: {}", student.getId());
        return student;
    }

    private User createTutor(SignupRequest request, String normalizedEmail, ZoneId timeZone) {
        Tutor tutor = userFactory.createTutor(
            request.getName(),
            normalizedEmail,
            passwordEncoder.encode(request.getPassword()),
            request.getHourlyRate(),
            request.getDescription());
        tutor.setTimeZone(timeZone);
        tutorRepository.save(tutor);

        logger.debug("Tutor created successfully: {}", tutor.getId());
        return tutor;
    }

    private ZoneId parseTimezone(String timezoneId) throws InvalidTimezoneException {
        if (timezoneId == null || timezoneId.trim().isEmpty()) {
            logger.debug("No timezone provided, using system default");
            return ZoneId.systemDefault();
        }

        try {
            return ZoneId.of(timezoneId);
        } catch (DateTimeException e) {
            logger.warn("Invalid timezone: {}", timezoneId);
            throw new InvalidTimezoneException(timezoneId);
        }
    }   
}