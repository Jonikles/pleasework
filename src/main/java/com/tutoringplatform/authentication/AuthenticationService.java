package com.tutoringplatform.security.authentication;

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
import java.time.ZoneId;

@Service
public class AuthenticationService {

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

    public AuthResponse login(String email, String password) throws Exception {
        // Find user by email
        User user = authenticationRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("Invalid email or password");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid email or password");
        }

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
    public AuthResponse signup(SignupRequest request) throws Exception {
        // Validate email doesn't exist
        if (authenticationRepository.emailExists(request.getEmail())) {
            throw new Exception("Email already exists");
        }

        // Validate timezone
        ZoneId timeZone;
        try {
            timeZone = request.getTimeZoneId() != null ? ZoneId.of(request.getTimeZoneId()) : ZoneId.systemDefault();
        } catch (Exception e) {
            throw new Exception("Invalid timezone");
        }

        User user;
        double balance = 0;
        double hourlyRate = 0;

        if ("STUDENT".equalsIgnoreCase(request.getUserType())) {
            // Create student
            Student student = userFactory.createStudent(
                    request.getName(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()));
            student.setTimeZone(timeZone);
            studentRepository.save(student);
            user = student;
            balance = student.getBalance();

        } else if ("TUTOR".equalsIgnoreCase(request.getUserType())) {
            // Validate tutor-specific fields
            if (request.getHourlyRate() <= 0) {
                throw new Exception("Hourly rate must be positive");
            }
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                throw new Exception("Description is required for tutors");
            }

            // Create tutor
            Tutor tutor = userFactory.createTutor(
                    request.getName(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getHourlyRate(),
                    request.getDescription());
            tutor.setTimeZone(timeZone);
            tutorRepository.save(tutor);
            user = tutor;
            hourlyRate = tutor.getHourlyRate();

        } else {
            throw new Exception("Invalid user type. Must be STUDENT or TUTOR");
        }

        return dtoMapper.toAuthResponse(user, balance, hourlyRate);
    }
}