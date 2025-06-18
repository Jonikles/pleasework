package com.tutoringplatform.services;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.file.FileService;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.review.ReviewService;
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;
import com.tutoringplatform.shared.dto.response.TutorProfileResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.user.tutor.exceptions.TutorHasBookingsException;
import com.tutoringplatform.user.tutor.exceptions.TutorTeachesSubjectException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private ITutorRepository tutorRepository;
    @Mock
    private SubjectService subjectService;
    @Mock
    private BookingService bookingService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private AvailabilityService availabilityService;
    @Mock
    private FileService fileService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DTOMapper dtoMapper;

    private TutorService tutorService;
    private Tutor tutor;
    private final String tutorId = "tutor123";
    private final String studentId = "student123";
    private final Subject subject = new Subject("Math", "Science");
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final int durationHours = 1;
    private final double hourlyRate = 100.0;

    @BeforeEach
    void setUp() {
        tutorService = new TutorService(tutorRepository, subjectService, bookingService, reviewService,
                availabilityService, fileService, passwordEncoder, dtoMapper);
        tutor = new Tutor("Jane Doe", "jane.doe@example.com", "password", 50.0, "Expert tutor");
        tutor.setId(tutorId);
    }

    @Test
    void getTutorProfile_success() throws UserNotFoundException, NoCompletedBookingsException {
        // Arrange
        TutorProfileResponse expectedResponse = new TutorProfileResponse();
        Review review = new Review("student1", tutorId, 5, "Great!");
        Booking completedBooking = new Booking(studentId, tutorId, subject, dateTime, durationHours, hourlyRate);
        completedBooking.setStatus(Booking.BookingStatus.COMPLETED);

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(reviewService.getTutorReviews(tutorId)).thenReturn(Collections.singletonList(review));
        when(bookingService.getTutorBookingList(tutorId)).thenReturn(Collections.singletonList(completedBooking));
        when(availabilityService.getAvailability(tutorId)).thenReturn(null);
        when(dtoMapper.toTutorProfileResponse(any(), anyDouble(), anyInt(), any(), any(), anyInt(), any()))
                .thenReturn(expectedResponse);

        // Act
        TutorProfileResponse actualResponse = tutorService.getTutorProfile(tutorId);

        // Assert
        assertNotNull(actualResponse);
        verify(tutorRepository).findById(tutorId);
    }

    @Test
    void addSubjectToTutor_success() throws Exception {
        // Arrange
        String subjectId = "subject456";
        Subject subject = new Subject("Math", "Mathematics");
        subject.setId(subjectId);
        TutorProfileResponse expectedResponse = new TutorProfileResponse();

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);
        when(reviewService.getTutorReviews(tutorId)).thenReturn(Collections.emptyList());
        when(bookingService.getTutorBookingList(tutorId)).thenReturn(Collections.emptyList());
        when(dtoMapper.toTutorProfileResponse(any(), anyDouble(), anyInt(), any(), any(), anyInt(), any()))
                .thenReturn(expectedResponse);

        // Act
        TutorProfileResponse actualResponse = tutorService.addSubjectToTutor(tutorId, subjectId);

        // Assert
        assertNotNull(actualResponse);
        verify(tutorRepository).update(tutor);
        assertTrue(tutor.getSubjects().contains(subject));
    }

    @Test
    void addSubjectToTutor_alreadyTeaches_throwsException() throws Exception {
        // Arrange
        String subjectId = "subject456";
        Subject subject = new Subject("Math", "Mathematics");
        subject.setId(subjectId);
        tutor.addSubject(subject);

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);

        // Act & Assert
        assertThrows(TutorTeachesSubjectException.class, () -> tutorService.addSubjectToTutor(tutorId, subjectId));
        verify(tutorRepository, never()).update(any());
    }

    @Test
    void removeSubjectFromTutor_success() throws Exception {
        // Arrange
        String subjectId = "subject456";
        Subject subject = new Subject("Math", "Mathematics");
        subject.setId(subjectId);
        tutor.addSubject(subject);
        TutorProfileResponse expectedResponse = new TutorProfileResponse();

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);
        when(bookingService.getTutorBookingsBySubject(tutorId, subjectId)).thenReturn(Collections.emptyList());
        when(reviewService.getTutorReviews(tutorId)).thenReturn(Collections.emptyList());
        when(bookingService.getTutorBookingList(tutorId)).thenReturn(Collections.emptyList());
        when(dtoMapper.toTutorProfileResponse(any(), anyDouble(), anyInt(), any(), any(), anyInt(), any()))
                .thenReturn(expectedResponse);

        // Act
        tutorService.removeSubjectFromTutor(tutorId, subjectId);

        // Assert
        verify(tutorRepository).update(tutor);
        assertFalse(tutor.getSubjects().contains(subject));
    }

    @Test
    void removeSubjectFromTutor_withExistingBookings_throwsException() throws Exception {
        // Arrange
        String subjectId = "subject456";
        Subject subject = new Subject("Math", "Mathematics");
        subject.setId(subjectId);
        tutor.addSubject(subject);

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);
        when(bookingService.getTutorBookingsBySubject(tutorId, subjectId)).thenReturn(Arrays.asList(new Booking(studentId, tutorId, subject, dateTime, durationHours, hourlyRate)));

        // Act & Assert
        assertThrows(TutorHasBookingsException.class, () -> tutorService.removeSubjectFromTutor(tutorId, subjectId));
        verify(tutorRepository, never()).update(any());
    }
}