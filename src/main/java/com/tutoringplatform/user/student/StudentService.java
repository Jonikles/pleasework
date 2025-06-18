package com.tutoringplatform.user.student;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.file.FileService;
import com.tutoringplatform.shared.dto.request.UpdateProfileRequest;
import com.tutoringplatform.shared.dto.response.StudentProfileResponse;
import com.tutoringplatform.shared.dto.response.ValueResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.user.UserService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class StudentService extends UserService<Student> {

    private final IBookingRepository bookingRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Autowired
    public StudentService(
            IStudentRepository studentRepository,
            IBookingRepository bookingRepository,
            FileService fileService,
            PasswordEncoder passwordEncoder,
            DTOMapper dtoMapper) {
        super(studentRepository);
        this.bookingRepository = bookingRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.dtoMapper = dtoMapper;
    }

    public StudentProfileResponse getStudentProfile(String studentId) throws Exception {
        Student student = findById(studentId);

        // Calculate joined date (would normally come from audit fields)
        LocalDate joinedDate = LocalDate.now().minusYears(1); // Placeholder

        // Count total sessions
        List<Booking> bookings = bookingRepository.findByStudentId(studentId);
        int totalSessions = bookings.size();

        return dtoMapper.toStudentProfileResponse(student, joinedDate, totalSessions);
    }

    @Transactional
    public StudentProfileResponse updateStudentProfile(String studentId, UpdateProfileRequest request)
            throws Exception {
        Student student = findById(studentId);

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            student.setName(request.getName());
        }

        // Update email if provided and not taken
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Student existing = repository.findByEmail(request.getEmail());
            if (existing != null && !existing.getId().equals(studentId)) {
                throw new Exception("Email already in use");
            }
            student.setEmail(request.getEmail());
        }

        // Update password if provided with current password verification
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new Exception("Current password required to change password");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), student.getPassword())) {
                throw new Exception("Current password is incorrect");
            }

            student.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update timezone if provided
        if (request.getTimeZoneId() != null) {
            try {
                ZoneId zone = ZoneId.of(request.getTimeZoneId());
                student.setTimeZone(zone);
            } catch (Exception e) {
                throw new Exception("Invalid timezone");
            }
        }

        repository.update(student);

        return getStudentProfile(studentId);
    }

    @Transactional
    public Map<String, String> updateProfilePicture(String studentId, MultipartFile file) throws Exception {
        Student student = findById(studentId);

        // Delete old profile picture if exists
        if (student.getProfilePictureId() != null) {
            try {
                fileService.deleteFile(student.getProfilePictureId());
            } catch (Exception e) {
                // Log error but continue
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
    public ValueResponse<Double> addFunds(String studentId, double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Amount must be positive");
        }

        Student student = findById(studentId);
        double newBalance = student.getBalance() + amount;
        student.setBalance(newBalance);
        repository.update(student);

        // Log transaction (would normally create a transaction record)
        // transactionService.createDeposit(studentId, amount);

        return dtoMapper.toValueResponse(newBalance);
    }

    public ValueResponse<Double> getBalance(String studentId) throws Exception {
        Student student = findById(studentId);
        return dtoMapper.toValueResponse(student.getBalance());
    }
}