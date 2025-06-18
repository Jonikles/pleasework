package com.tutoringplatform.user.student;

import com.tutoringplatform.file.FileService;
import com.tutoringplatform.shared.dto.request.UpdateProfileRequest;
import com.tutoringplatform.shared.dto.response.StudentProfileResponse;
import com.tutoringplatform.shared.dto.response.ValueResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.user.UserService;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.authentication.exceptions.EmailAlreadyExistsException;
import com.tutoringplatform.authentication.exceptions.InvalidTimezoneException;
import com.tutoringplatform.user.exceptions.InvalidPasswordException;
import com.tutoringplatform.user.student.exceptions.InvalidFundAmountException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class StudentService extends UserService<Student> {

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final BookingService bookingService;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Autowired
    public StudentService(
            IStudentRepository studentRepository,
            BookingService bookingService,
            FileService fileService,
            PasswordEncoder passwordEncoder,
            DTOMapper dtoMapper) {
        super(studentRepository);
        this.bookingService = bookingService;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.dtoMapper = dtoMapper;
    }

    public StudentProfileResponse getStudentProfile(String studentId) throws UserNotFoundException {
        logger.debug("Getting student profile for student: {}", studentId);
        Student student = findById(studentId);

        // Calculate joined date (would normally come from audit fields)
        LocalDate joinedDate = LocalDate.now().minusYears(1); // Placeholder

        // Count total sessions
        List<Booking> bookings = bookingService.getStudentBookingList(studentId);
        int totalSessions = bookings.size();

        logger.info("Student profile found successfully for student: {}", studentId);
        return dtoMapper.toStudentProfileResponse(student, joinedDate, totalSessions);
    }

    @Transactional
    public StudentProfileResponse updateStudentProfile(String studentId, UpdateProfileRequest request)
            throws UserNotFoundException, EmailAlreadyExistsException, InvalidPasswordException, InvalidTimezoneException {
        logger.debug("Updating student profile for student: {}", studentId);
        Student student = findById(studentId);

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            student.setName(request.getName());
        }

        // Update email if provided and not taken
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Student existing = repository.findByEmail(request.getEmail());
            if (existing != null && !existing.getId().equals(studentId)) {

                throw new EmailAlreadyExistsException(request.getEmail());
            }
            student.setEmail(request.getEmail());
        }

        // Update password if provided with current password verification
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new InvalidPasswordException("Current password is required");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), student.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }

            student.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update timezone if provided
        if (request.getTimeZoneId() != null) {
            try {
                ZoneId zone = ZoneId.of(request.getTimeZoneId());
                student.setTimeZone(zone);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid timezone: {}", request.getTimeZoneId());
                throw new InvalidTimezoneException(request.getTimeZoneId());
            }
        }

        repository.update(student);

        logger.info("Student profile updated successfully for student: {}", studentId);
        return getStudentProfile(studentId);
    }

    @Transactional
    public Map<String, String> updateProfilePicture(String studentId, MultipartFile file) throws UserNotFoundException {
        Student student = findById(studentId);

        // Delete old profile picture if exists
        if (student.getProfilePictureId() != null) {
            try {
                fileService.deleteFile(student.getProfilePictureId());
            } catch (Exception e) {
                logger.warn("Couldn't delete old profile picture: {}", e.getMessage());
            }
        }

        // Store new profile picture
        String fileId = fileService.storeFile(studentId, file, "profile");
        student.setProfilePictureId(fileId);
        repository.update(student);

        Map<String, String> result = new HashMap<>();
        result.put("profilePictureId", fileId);
        result.put("profilePictureUrl", "/api/files/" + fileId);

        return result;
    }

    @Transactional
    public ValueResponse<Double> addFunds(String studentId, double amount) throws UserNotFoundException, InvalidFundAmountException {
        if (amount <= 0) {
            logger.warn("Invalid fund amount: {}", amount);
            throw new InvalidFundAmountException(amount);
        }

        Student student = findById(studentId);
        double newBalance = student.getBalance() + amount;
        student.setBalance(newBalance);
        repository.update(student);

        // Log transaction (would normally create a transaction record)
        // transactionService.createDeposit(studentId, amount);

        return dtoMapper.toValueResponse(newBalance);
    }

    public ValueResponse<Double> getBalance(String studentId) throws UserNotFoundException {
        Student student = findById(studentId);
        return dtoMapper.toValueResponse(student.getBalance());
    }
}