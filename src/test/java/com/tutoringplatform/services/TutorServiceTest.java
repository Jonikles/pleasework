package com.tutoringplatform.services;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.file.FileService;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.review.ReviewService;
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;
import com.tutoringplatform.shared.dto.request.UpdateProfileRequest;
import com.tutoringplatform.shared.dto.response.ValueResponse;
import com.tutoringplatform.shared.dto.response.TutorProfileResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.subject.ISubjectRepository;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.user.tutor.exceptions.TutorHasBookingsException;
import com.tutoringplatform.user.tutor.exceptions.TutorNotTeachingSubjectException;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private ITutorRepository tutorRepository;
    @Mock
    private ISubjectRepository subjectRepository;
    @Mock
    private IBookingRepository bookingRepository;
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
        tutorService = new TutorService(tutorRepository, subjectRepository, bookingRepository, reviewService,
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
        when(bookingRepository.findByTutorId(tutorId)).thenReturn(Collections.singletonList(completedBooking));
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
        when(subjectRepository.findById(subjectId)).thenReturn(subject);
        when(reviewService.getTutorReviews(tutorId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByTutorId(tutorId)).thenReturn(Collections.emptyList());
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
        when(subjectRepository.findById(subjectId)).thenReturn(subject);

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
        when(subjectRepository.findById(subjectId)).thenReturn(subject);
        when(bookingRepository.findByTutorIdAndSubjectId(tutorId, subjectId)).thenReturn(Collections.emptyList());
        when(reviewService.getTutorReviews(tutorId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByTutorId(tutorId)).thenReturn(Collections.emptyList());
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
        when(subjectRepository.findById(subjectId)).thenReturn(subject);
        when(bookingRepository.findByTutorIdAndSubjectId(tutorId, subjectId)).thenReturn(Arrays.asList(new Booking(studentId, tutorId, subject, dateTime, durationHours, hourlyRate)));

        // Act & Assert
        assertThrows(TutorHasBookingsException.class, () -> tutorService.removeSubjectFromTutor(tutorId, subjectId));
        verify(tutorRepository, never()).update(any());
    }

    @Test
    void updateTutorProfile_Success() throws Exception {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Jane Smith");
        request.setHourlyRate(75.0);
        request.setDescription("Updated description with more experience details and qualifications.");

        TutorProfileResponse expectedResponse = new TutorProfileResponse();

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(reviewService.getTutorReviews(tutorId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByTutorId(tutorId)).thenReturn(Collections.emptyList());
        when(dtoMapper.toTutorProfileResponse(any(), anyDouble(), anyInt(), any(), any(), anyInt(), any()))
                .thenReturn(expectedResponse);

        // Act
        TutorProfileResponse result = tutorService.updateTutorProfile(tutorId, request);

        // Assert
        assertNotNull(result);
        verify(tutorRepository).update(argThat(t -> t.getName().equals("Jane Smith") &&
                t.getHourlyRate() == 75.0 &&
                t.getDescription().contains("Updated description")));
    }

    @Test
    void getAverageRating_NoReviews_ReturnsZero() throws Exception {
        // Arrange
        when(reviewService.getTutorReviews(tutorId)).thenReturn(Collections.emptyList());

        when(dtoMapper.toValueResponse(anyDouble()))
                .thenAnswer(invocation -> {
                    ValueResponse<Double> response = new ValueResponse<>();
                    response.setValue(invocation.getArgument(0));
                    return response;
                });

        // Act
        ValueResponse<Double> result = tutorService.getAverageRating(tutorId);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getValue());
    }

    @Test
    void getAverageRating_MultipleReviews_CalculatesCorrectly() throws Exception {
        // Arrange
        Review review1 = new Review("student1", tutorId, 5, "Great!");
        Review review2 = new Review("student2", tutorId, 3, "Good");
        Review review3 = new Review("student3", tutorId, 4, "Very good");

        when(reviewService.getTutorReviews(tutorId)).thenReturn(Arrays.asList(review1, review2, review3));

        when(dtoMapper.toValueResponse(anyDouble()))
                .thenAnswer(invocation -> {
                    ValueResponse<Double> response = new ValueResponse<>();
                    response.setValue(invocation.getArgument(0));
                    return response;
                });
        
        // Act
        ValueResponse<Double> result = tutorService.getAverageRating(tutorId);

        // Assert
        assertNotNull(result);
        assertEquals(4.0, result.getValue());
    }

    @Test
    void getAllTutorProfiles_Success() throws Exception {
        // Arrange
        Tutor tutor1 = new Tutor("Tutor 1", "tutor1@email.com", "password", 40.0, "Math tutor");
        tutor1.setId("tutor1");
        Tutor tutor2 = new Tutor("Tutor 2", "tutor2@email.com", "password", 60.0, "Science tutor");
        tutor2.setId("tutor2");

        TutorProfileResponse response1 = new TutorProfileResponse();
        response1.setId("tutor1");
        TutorProfileResponse response2 = new TutorProfileResponse();
        response2.setId("tutor2");

        when(tutorRepository.findAll()).thenReturn(Arrays.asList(tutor1, tutor2));
        when(tutorRepository.findById("tutor1")).thenReturn(tutor1);
        when(tutorRepository.findById("tutor2")).thenReturn(tutor2);
        when(reviewService.getTutorReviews(anyString())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByTutorId(anyString())).thenReturn(Collections.emptyList());
        when(dtoMapper.toTutorProfileResponse(any(), anyDouble(), anyInt(), any(), any(), anyInt(), any()))
                .thenReturn(response1, response2);

        // Act
        List<TutorProfileResponse> results = tutorService.getAllTutorProfiles();

        // Assert
        assertEquals(2, results.size());
        verify(tutorRepository, times(2)).findById(anyString());
    }

    @Test
    void removeSubjectFromTutor_NotTeachingSubject_ThrowsException() throws Exception {
        // Arrange
        String subjectId = "subject456";
        Subject subject = new Subject("Physics", "Science");
        subject.setId(subjectId);
        // Tutor doesn't have this subject

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectRepository.findById(subjectId)).thenReturn(subject);

        // Act & Assert
        assertThrows(TutorNotTeachingSubjectException.class,
                () -> tutorService.removeSubjectFromTutor(tutorId, subjectId));
    }
}