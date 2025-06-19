package com.tutoringplatform.services;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.review.IReviewRepository;
import com.tutoringplatform.review.ReviewService;
import com.tutoringplatform.review.exceptions.InvalidRatingException;
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;
import com.tutoringplatform.review.exceptions.ReviewNotFoundException;
import com.tutoringplatform.shared.dto.request.CreateReviewRequest;
import com.tutoringplatform.shared.dto.response.ReviewResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.StudentService;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.subject.Subject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private IReviewRepository reviewRepository;
    @Mock
    private StudentService studentService;
    @Mock
    private ITutorRepository tutorRepository;
    @Mock
    private IBookingRepository bookingRepository;
    @Mock
    private DTOMapper dtoMapper;

    private ReviewService reviewService;

    private CreateReviewRequest createReviewRequest;
    private Student student;
    private Tutor tutor;
    private final String studentId = "student123";
    private final String tutorId = "tutor456";
    private final Subject subject = new Subject("Math", "Science");
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final int durationHours = 1;
    private final double hourlyRate = 100.0;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, studentService, tutorRepository, bookingRepository, dtoMapper);

        student = new Student("John", "john@email.com", "pass");
        student.setId(studentId);

        tutor = new Tutor("Jane", "jane@email.com", "pass", 50.0, "desc");
        tutor.setId(tutorId);

        createReviewRequest = new CreateReviewRequest();
        createReviewRequest.setStudentId(studentId);
        createReviewRequest.setTutorId(tutorId);
        createReviewRequest.setRating(5);
        createReviewRequest.setComment("This is a sufficiently long and valid test comment for review creation.");
    }

    @Test
    void createReview_newReview_success() throws Exception {
        // Arrange
        Booking completedBooking = new Booking(studentId, tutorId, subject, dateTime, durationHours, hourlyRate);
        completedBooking.setStatus(Booking.BookingStatus.COMPLETED);
        ReviewResponse expectedResponse = new ReviewResponse();

        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(bookingRepository.findByStudentIdAndTutorIdAndStatus(studentId, tutorId, Booking.BookingStatus.COMPLETED))
                .thenReturn(Collections.singletonList(completedBooking));
        when(reviewRepository.findByStudentIdAndTutorId(studentId, tutorId)).thenReturn(null);
        when(dtoMapper.toReviewResponse(any(Review.class), eq(student), eq(tutor))).thenReturn(expectedResponse);

        // Act
        ReviewResponse actualResponse = reviewService.createReview(createReviewRequest);

        // Assert
        assertNotNull(actualResponse);
        verify(reviewRepository).save(any(Review.class));
        verify(reviewRepository, never()).update(any());
    }

    @Test
    void createReview_existingReview_updatesReview() throws Exception {
        // Arrange
        Booking completedBooking = new Booking(studentId, tutorId, subject, dateTime, durationHours, hourlyRate);
        completedBooking.setStatus(Booking.BookingStatus.COMPLETED);
        Review existingReview = new Review(studentId, tutorId, 4, "Old comment.");
        ReviewResponse expectedResponse = new ReviewResponse();

        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(bookingRepository.findByStudentIdAndTutorIdAndStatus(studentId, tutorId, Booking.BookingStatus.COMPLETED))
                .thenReturn(Collections.singletonList(completedBooking));
        when(reviewRepository.findByStudentIdAndTutorId(studentId, tutorId)).thenReturn(existingReview);
        when(dtoMapper.toReviewResponse(any(Review.class), eq(student), eq(tutor))).thenReturn(expectedResponse);

        // Act
        ReviewResponse actualResponse = reviewService.createReview(createReviewRequest);

        // Assert
        assertNotNull(actualResponse);
        verify(reviewRepository, never()).save(any());
        verify(reviewRepository).update(existingReview);
        assertEquals(5, existingReview.getRating());
        assertEquals(createReviewRequest.getComment(), existingReview.getComment());
        assertEquals(5, existingReview.getRating());
    }

    @Test
    void createReview_noCompletedBookings_throwsException() throws UserNotFoundException {
        // Arrange
        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(bookingRepository.findByStudentIdAndTutorIdAndStatus(studentId, tutorId, Booking.BookingStatus.COMPLETED))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(NoCompletedBookingsException.class, () -> reviewService.createReview(createReviewRequest));
    }

    @Test
    void createReview_invalidRating_throwsException() {
        // Arrange
        createReviewRequest.setRating(6);

        // Act & Assert
        assertThrows(InvalidRatingException.class, () -> reviewService.createReview(createReviewRequest));
    }

    @Test
    void createReview_shortComment_throwsException() {
        // Arrange
        createReviewRequest.setComment("Too short."); // Invalid comment

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(createReviewRequest));
    }

    @Test
    void deleteReview_success() throws ReviewNotFoundException {
        // Arrange
        String reviewId = "review123";
        Review review = new Review(studentId, tutorId, 5, "This is a review comment.");
        when(reviewRepository.findById(reviewId)).thenReturn(review);

        // Act
        reviewService.deleteReview(reviewId);

        // Assert
        verify(reviewRepository).delete(reviewId);
    }

    @Test
    void deleteReview_notFound_throwsException() {
        // Arrange
        String reviewId = "nonexistent";
        when(reviewRepository.findById(reviewId)).thenReturn(null);

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(reviewId));
    }

    @Test
    void getTutorReviews_success() throws Exception {
        // Arrange
        Review review = new Review(studentId, tutorId, 5, "This is a review comment.");
        List<Review> reviews = Collections.singletonList(review);

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(reviewRepository.getTutorReviews(tutorId)).thenReturn(reviews);

        // Act
        List<Review> actualReviews = reviewService.getTutorReviews(tutorId);

        // Assert
        assertFalse(actualReviews.isEmpty());
        assertEquals(reviews, actualReviews);
        verify(reviewRepository).getTutorReviews(tutorId);
    }

    @Test
    void createReview_nullRequest_throwsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reviewService.createReview(null));
    }

    @Test
    void createReview_longComment_throwsException() {
        // Arrange
        createReviewRequest.setComment("a".repeat(1001)); // Too long

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reviewService.createReview(createReviewRequest));
    }

    @Test
    void getTutorReviewsResponse_sortsReviewsByDate() throws Exception {
        // Arrange
        Review oldReview = new Review(studentId, tutorId, 4, "Old review");
        oldReview.setTimestamp(LocalDateTime.now().minusDays(5));

        Review newReview = new Review("student2", tutorId, 5, "New review");
        newReview.setTimestamp(LocalDateTime.now());

        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(reviewRepository.getTutorReviews(tutorId)).thenReturn(Arrays.asList(oldReview, newReview));
        when(studentService.findById(anyString())).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(dtoMapper.toReviewResponse(any(), any(), any())).thenAnswer(invocation -> {
            ReviewResponse response = new ReviewResponse();
            Review review = invocation.getArgument(0);
            response.setCreatedAt(review.getTimestamp());
            return response;
        });

        // Act
        List<ReviewResponse> responses = reviewService.getTutorReviewsResponse(tutorId);

        // Assert
        assertEquals(2, responses.size());
        assertTrue(responses.get(0).getCreatedAt().isAfter(responses.get(1).getCreatedAt()));
    }

    @Test
    void createReview_multipleCompletedBookings_allowsReview() throws Exception {
        // Arrange
        Booking booking1 = new Booking(studentId, tutorId, subject, dateTime.minusDays(10), 1, 50);
        booking1.setStatus(Booking.BookingStatus.COMPLETED);

        Booking booking2 = new Booking(studentId, tutorId, subject, dateTime.minusDays(5), 1, 50);
        booking2.setStatus(Booking.BookingStatus.COMPLETED);

        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(bookingRepository.findByStudentIdAndTutorIdAndStatus(studentId, tutorId, Booking.BookingStatus.COMPLETED))
                .thenReturn(Arrays.asList(booking1, booking2));
        when(reviewRepository.findByStudentIdAndTutorId(studentId, tutorId)).thenReturn(null);

        // Act
        reviewService.createReview(createReviewRequest);

        // Assert
        verify(reviewRepository).save(any(Review.class));
    }
}