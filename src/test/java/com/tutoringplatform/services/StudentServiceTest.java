package com.tutoringplatform.services;

import com.tutoringplatform.authentication.exceptions.EmailAlreadyExistsException;
import com.tutoringplatform.authentication.exceptions.InvalidTimezoneException;
import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.file.FileService;
import com.tutoringplatform.shared.dto.request.UpdateProfileRequest;
import com.tutoringplatform.shared.dto.response.StudentProfileResponse;
import com.tutoringplatform.shared.dto.response.ValueResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.StudentService;
import com.tutoringplatform.user.student.exceptions.InvalidFundAmountException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.tutoringplatform.file.exception.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private IStudentRepository studentRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private FileService fileService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DTOMapper dtoMapper;

    private StudentService studentService;
    private Student student;
    private final String studentId = "student123";
    private final String tutorId = "tutor123";
    private final Subject subject = new Subject("Math", "Science");
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final int durationHours = 1;
    private final double hourlyRate = 100.0;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, bookingService, fileService, passwordEncoder, dtoMapper);
        student = new Student("John Doe", "john.doe@example.com", "encodedPassword");
        student.setId(studentId);
        student.setBalance(100.0);
        student.setTimeZone(ZoneId.of("UTC"));
    }

    @Test
    void getStudentProfile_success() throws Exception {
        // Arrange
        StudentProfileResponse expectedResponse = new StudentProfileResponse();
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(bookingService.getStudentBookingList(studentId)).thenReturn(Collections.singletonList(new Booking(studentId, tutorId, subject, dateTime, durationHours, hourlyRate)));
        when(dtoMapper.toStudentProfileResponse(eq(student), any(), eq(1))).thenReturn(expectedResponse);

        // Act
        StudentProfileResponse actualResponse = studentService.getStudentProfile(studentId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(studentRepository).findById(studentId);
        verify(bookingService).getStudentBookingList(studentId);
    }

    @Test
    void updateStudentProfile_success() throws Exception {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Johnathan Doe");
        request.setEmail("john.doe.new@example.com");
        request.setPassword("newPassword");
        request.setCurrentPassword("oldPassword");
        request.setTimeZoneId("America/New_York");

        StudentProfileResponse expectedResponse = new StudentProfileResponse();

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(studentRepository.findByEmail(request.getEmail())).thenReturn(null);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(bookingService.getStudentBookingList(studentId)).thenReturn(Collections.emptyList());
        when(dtoMapper.toStudentProfileResponse(any(Student.class), any(), anyInt())).thenReturn(expectedResponse);

        // Act
        studentService.updateStudentProfile(studentId, request);

        // Assert
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).update(studentCaptor.capture());
        Student updatedStudent = studentCaptor.getValue();
        assertEquals("Johnathan Doe", updatedStudent.getName());
        assertEquals("john.doe.new@example.com", updatedStudent.getEmail());
        assertEquals("newEncodedPassword", updatedStudent.getPassword());
        assertEquals(ZoneId.of("America/New_York"), updatedStudent.getTimeZone());
    }

    @Test
    void updateStudentProfile_emailAlreadyExists_throwsException() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("taken@example.com");

        Student otherStudent = new Student("John Doe", "john.doe@example.com", "encodedPassword");
        otherStudent.setId("otherStudent");

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(studentRepository.findByEmail("taken@example.com")).thenReturn(otherStudent);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> studentService.updateStudentProfile(studentId, request));
    }

    @Test
    void updateStudentProfile_invalidTimezone_throwsException() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setTimeZoneId("Invalid/Timezone");

        when(studentRepository.findById(studentId)).thenReturn(student);

        // Act & Assert
        assertThrows(InvalidTimezoneException.class, () -> studentService.updateStudentProfile(studentId, request));
    }

    @Test
    void updateProfilePicture_success() throws UserNotFoundException, IOException, FileNotFoundException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        String newFileId = "fileId456";
        student.setProfilePictureId("oldFileId123");

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(fileService.storeFile(studentId, mockFile, "profile")).thenReturn(newFileId);

        // Act
        Map<String, String> result = studentService.updateProfilePicture(studentId, mockFile);

        // Assert
        verify(fileService).deleteFile("oldFileId123");
        verify(fileService).storeFile(studentId, mockFile, "profile");
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).update(studentCaptor.capture());
        assertEquals(newFileId, studentCaptor.getValue().getProfilePictureId());
        assertEquals(newFileId, result.get("profilePictureId"));
    }

    @Test
    void addFunds_success() throws UserNotFoundException, InvalidFundAmountException {
        // Arrange
        double amountToAdd = 50.0;
        ValueResponse<Double> expectedResponse = new ValueResponse<>();
        expectedResponse.setValue(150.0);

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(dtoMapper.toValueResponse(150.0)).thenReturn(expectedResponse);

        // Act
        ValueResponse<Double> actualResponse = studentService.addFunds(studentId, amountToAdd);

        // Assert
        assertEquals(150.0, actualResponse.getValue());
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).update(studentCaptor.capture());
        assertEquals(150.0, studentCaptor.getValue().getBalance());
    }

    @Test
    void addFunds_withNegativeAmount_throwsException() {
        // Act & Assert
        assertThrows(InvalidFundAmountException.class, () -> studentService.addFunds(studentId, -50.0));
    }

    @Test
    void getBalance_success() throws UserNotFoundException {
        // Arrange
        ValueResponse<Double> expectedResponse = new ValueResponse<>();
        expectedResponse.setValue(100.0);
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(dtoMapper.toValueResponse(100.0)).thenReturn(expectedResponse);

        // Act
        ValueResponse<Double> actualResponse = studentService.getBalance(studentId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(100.0, actualResponse.getValue());
        verify(studentRepository).findById(studentId);
    }
}