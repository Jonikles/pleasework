package com.tutoringplatform.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tutoringplatform.models.*;
import com.tutoringplatform.observer.*;
import com.tutoringplatform.repositories.interfaces.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private IBookingRepository bookingRepository;

    @Mock
    private IStudentRepository studentRepository;

    @Mock
    private ITutorRepository tutorRepository;

    @Mock
    private BookingObserver mockObserver;

    private BookingService bookingService;
    private Student testStudent;
    private Tutor testTutor;
    private Subject testSubject;
    private LocalDateTime bookingTime;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, studentRepository, tutorRepository);
        bookingService.addObserver(mockObserver);

        testStudent = new Student("John Doe", "john@student.com", "password123");
        testTutor = new Tutor("Jane Smith", "jane@tutor.com", "password123", 50.0, "Math tutor");
        testSubject = new Subject("Math", "Mathematics");
        bookingTime = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0);

        testTutor.addSubject(testSubject);
        String dayOfWeek = bookingTime.getDayOfWeek().name();
        dayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();

        testTutor.addAvailability(dayOfWeek, 15);
        testTutor.addAvailability(dayOfWeek, 16);
    }

    @Test
    @DisplayName("Should create booking successfully")
    void testCreateBooking() throws Exception {
        when(studentRepository.findById("student123")).thenReturn(testStudent);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);
        when(bookingRepository.findByTutorId("tutor456")).thenReturn(new ArrayList<>());

        Booking booking = bookingService.createBooking("student123", "tutor456",
                testSubject, bookingTime, 2);

        assertNotNull(booking);
        assertEquals("student123", booking.getStudentId());
        assertEquals("tutor456", booking.getTutorId());
        assertEquals(100.0, booking.getTotalCost());
        assertEquals(Booking.BookingStatus.PENDING, booking.getStatus());
        verify(bookingRepository).save(any(Booking.class));
        verify(mockObserver).update(any(BookingEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void testCreateBookingStudentNotFound() {
        when(studentRepository.findById("invalid")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            bookingService.createBooking("invalid", "tutor456", testSubject, bookingTime, 2);
        }, "Student not found");
    }

    @Test
    @DisplayName("Should throw exception when tutor doesn't teach subject")
    void testCreateBookingTutorDoesntTeachSubject() {
        Subject otherSubject = new Subject("Physics", "Science");
        when(studentRepository.findById("student123")).thenReturn(testStudent);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);

        assertThrows(Exception.class, () -> {
            bookingService.createBooking("student123", "tutor456",
                    otherSubject, bookingTime, 2);
        }, "Tutor does not teach this subject");
    }

    @Test
    @DisplayName("Should throw exception when tutor not available")
    void testCreateBookingTutorNotAvailable() {
        LocalDateTime unavailableTime = LocalDateTime.now().plusDays(1).withHour(20).withMinute(0);
        when(studentRepository.findById("student123")).thenReturn(testStudent);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);

        assertThrows(Exception.class, () -> {
            bookingService.createBooking("student123", "tutor456",
                    testSubject, unavailableTime, 2);
        }, "Tutor is not available at this time");
    }

    @Test
    @DisplayName("Should throw exception for time conflict")
    void testCreateBookingTimeConflict() throws Exception {
        Booking existingBooking = new Booking("student999", "tutor456", testSubject,
                bookingTime, 2, 50.0);
        existingBooking.setStatus(Booking.BookingStatus.CONFIRMED);

        when(studentRepository.findById("student123")).thenReturn(testStudent);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);
        when(bookingRepository.findByTutorId("tutor456")).thenReturn(Arrays.asList(existingBooking));

        assertThrows(Exception.class, () -> {
            bookingService.createBooking("student123", "tutor456",
                    testSubject, bookingTime.plusMinutes(30), 1);
        }, "Time slot already booked");
    }

    @Test
    @DisplayName("Should confirm booking successfully")
    void testConfirmBooking() throws Exception {
        Booking booking = new Booking("student123", "tutor456", testSubject,
                bookingTime, 2, 50.0);
        Payment payment = new Payment(booking.getId(), 100.0);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        when(bookingRepository.findById(booking.getId())).thenReturn(booking);
        when(studentRepository.findById("student123")).thenReturn(testStudent);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);

        bookingService.confirmBooking(booking.getId(), payment);

        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(payment, booking.getPayment());
        assertTrue(testStudent.getBookings().contains(booking));
        assertTrue(testTutor.getBookings().contains(booking));
        verify(bookingRepository).update(booking);
        verify(studentRepository).update(testStudent);
        verify(tutorRepository).update(testTutor);
        verify(mockObserver).update(any(BookingEvent.class));
    }

    @Test
    @DisplayName("Should cancel booking successfully")
    void testCancelBooking() throws Exception {
        Booking booking = new Booking("student123", "tutor456", testSubject,
                bookingTime, 2, 50.0);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(booking);
        when(studentRepository.findById("student123")).thenReturn(testStudent);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);

        bookingService.cancelBooking(booking.getId());

        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository).update(booking);
        verify(mockObserver).update(any(BookingEvent.class));
    }

    @Test
    @DisplayName("Should not cancel completed booking")
    void testCancelCompletedBooking() {
        Booking booking = new Booking("student123", "tutor456", testSubject,
                bookingTime, 2, 50.0);
        booking.setStatus(Booking.BookingStatus.COMPLETED);

        when(bookingRepository.findById(booking.getId())).thenReturn(booking);

        assertThrows(Exception.class, () -> {
            bookingService.cancelBooking(booking.getId());
        }, "Cannot cancel completed booking");
    }

    @Test
    @DisplayName("Should complete booking successfully")
    void testCompleteBooking() throws Exception {
        Booking booking = new Booking("student123", "tutor456", testSubject,
                bookingTime, 2, 50.0);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(booking);
        when(tutorRepository.findById("tutor456")).thenReturn(testTutor);
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        bookingService.completeBooking(booking.getId());

        assertEquals(Booking.BookingStatus.COMPLETED, booking.getStatus());
        assertEquals(100.0, testTutor.getEarnings());
        verify(bookingRepository).update(booking);
        verify(tutorRepository).update(testTutor);
        verify(mockObserver).update(any(BookingEvent.class));
    }

    @Test
    @DisplayName("Should find bookings by student")
    void testFindByStudent() {
        List<Booking> expectedBookings = Arrays.asList(
                new Booking("student123", "tutor1", testSubject, bookingTime, 1, 50.0),
                new Booking("student123", "tutor2", testSubject, bookingTime.plusDays(1), 2, 60.0));
        when(bookingRepository.findByStudentId("student123")).thenReturn(expectedBookings);

        List<Booking> result = bookingService.findByStudent("student123");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getStudentId().equals("student123")));
    }
}