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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private IReviewRepository reviewRepository;

    @Mock
    private IBookingRepository bookingRepository;

    @Mock
    private ITutorRepository tutorRepository;

    @Mock
    private IStudentRepository studentRepository;

    private ReviewService reviewService;
    private Booking completedBooking;
    private Student testStudent;
    private Tutor testTutor;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService();
        ReflectionTestUtils.setField(reviewService, "reviewRepository", reviewRepository);
        ReflectionTestUtils.setField(reviewService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(reviewService, "tutorRepository", tutorRepository);
        ReflectionTestUtils.setField(reviewService, "studentRepository", studentRepository);

        Subject subject = new Subject("Math", "Mathematics");
        completedBooking = new Booking("student123", "tutor456", subject,
                LocalDateTime.now().minusDays(1), 2, 50.0);
        completedBooking.setStatus(Booking.BookingStatus.COMPLETED);

        testStudent = new Student("John Doe", "john@student.com", "password123");
        testTutor = new Tutor("Jane Smith", "jane@tutor.com", "password123", 50.0, "Math tutor");
    }

    @Test
    @DisplayName("Should create review successfully")
    void testCreateReview() throws Exception {
        when(bookingRepository.findById("booking123")).thenReturn(completedBooking);
        when(reviewRepository.findByBookingId("booking123")).thenReturn(null);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        Review review = reviewService.createReview("booking123", 5, "Excellent tutor!");

        assertNotNull(review);
        assertEquals(5, review.getRating());
        assertEquals("Excellent tutor!", review.getComment());
        assertEquals("student123", review.getStudentId());
        assertEquals("tutor456", review.getTutorId());
        verify(reviewRepository).save(any(Review.class));
        verify(tutorRepository).update(testTutor);
        verify(studentRepository).update(testStudent);
        assertTrue(testTutor.getReviewsReceived().contains(review));
        assertTrue(testStudent.getReviewsGiven().contains(review));
    }

    @Test
    @DisplayName("Should throw exception when booking not found")
    void testCreateReviewBookingNotFound() {
        when(bookingRepository.findById("invalid")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            reviewService.createReview("invalid", 5, "Good");
        }, "Booking not found");
    }

    @Test
    @DisplayName("Should throw exception for non-completed booking")
    void testCreateReviewBookingNotCompleted() {
        Booking pendingBooking = new Booking("student123", "tutor456",
                new Subject("Math", "Mathematics"),
                LocalDateTime.now().plusDays(1), 2, 50.0);
        pendingBooking.setStatus(Booking.BookingStatus.PENDING);

        when(bookingRepository.findById("booking123")).thenReturn(pendingBooking);

        assertThrows(Exception.class, () -> {
            reviewService.createReview("booking123", 5, "Good");
        }, "Can only review completed bookings");
    }

    @Test
    @DisplayName("Should throw exception when review already exists")
    void testCreateReviewAlreadyExists() {
        Review existingReview = new Review("student123", "tutor456", "booking123", 4, "Good");

        when(bookingRepository.findById("booking123")).thenReturn(completedBooking);
        when(reviewRepository.findByBookingId("booking123")).thenReturn(existingReview);

        assertThrows(Exception.class, () -> {
            reviewService.createReview("booking123", 5, "Excellent!");
        }, "Review already exists for this booking");
    }

    @Test
    @DisplayName("Should validate rating range")
    void testCreateReviewInvalidRating() {
        when(bookingRepository.findById("booking123")).thenReturn(completedBooking);
        when(reviewRepository.findByBookingId("booking123")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview("booking123", 6, "Too high rating");
        }, "Rating must be between 1 and 5");

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview("booking123", 0, "Too low rating");
        }, "Rating must be between 1 and 5");
    }

    @Test
    @DisplayName("Should find reviews by tutor")
    void testFindByTutor() {
        List<Review> tutorReviews = Arrays.asList(
                new Review("student1", "tutor456", "booking1", 5, "Excellent!"),
                new Review("student2", "tutor456", "booking2", 4, "Very good"));

        when(reviewRepository.findByTutorId("tutor456")).thenReturn(tutorReviews);

        List<Review> result = reviewService.findByTutor("tutor456");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getTutorId().equals("tutor456")));
    }

    @Test
    @DisplayName("Should find reviews by student")
    void testFindByStudent() {
        List<Review> studentReviews = Arrays.asList(
                new Review("student123", "tutor1", "booking1", 5, "Great!"),
                new Review("student123", "tutor2", "booking2", 3, "Average"));

        when(reviewRepository.findByStudentId("student123")).thenReturn(studentReviews);

        List<Review> result = reviewService.findByStudent("student123");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getStudentId().equals("student123")));
    }

    @Test
    @DisplayName("Should calculate correct average rating for tutor")
    void testTutorAverageRating() throws Exception {
        when(bookingRepository.findById("booking123")).thenReturn(completedBooking);
        when(reviewRepository.findByBookingId("booking123")).thenReturn(null);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        Review review1 = new Review("student1", "tutor456", "booking1", 5, "Excellent!");
        Review review2 = new Review("student2", "tutor456", "booking2", 4, "Good");
        testTutor.addReview(review1);
        testTutor.addReview(review2);

        reviewService.createReview("booking123", 3, "Average");

        assertEquals(3, testTutor.getReviewsReceived().size());
        assertEquals(4.0, testTutor.getAverageRating());
    }
}