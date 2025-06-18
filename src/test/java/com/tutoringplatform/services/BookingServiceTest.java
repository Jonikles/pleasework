package com.tutoringplatform.services;

import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.payment.PaymentService;
import com.tutoringplatform.shared.dto.request.CreateBookingRequest;
import com.tutoringplatform.shared.dto.response.BookingDetailResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.student.StudentService;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.user.student.exceptions.InsufficientBalanceException;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.booking.Booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private IBookingRepository bookingRepository;
    @Mock
    private AvailabilityService availabilityService;
    @Mock
    private DTOMapper dtoMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private StudentService studentService;
    @Mock
    private TutorService tutorService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private PaymentService paymentService;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(
                bookingRepository, subjectService, availabilityService,
                paymentService, dtoMapper,
                eventPublisher, studentService, tutorService);
    }

    @Test
    void createBooking_Success() throws Exception {
        // Arrange
        String studentId = "student123";
        String tutorId = "tutor456";
        String subjectId = "subject789";
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setStudentId(studentId);
        request.setTutorId(tutorId);
        request.setSubjectId(subjectId);
        request.setDateTime(dateTime);
        request.setDurationHours(2);

        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        student.setBalance(200.0);
        student.setTimeZone(ZoneId.of("America/New_York"));

        Tutor tutor = new Tutor("Jane", "jane@email.com", "password", 50.0, "Math tutor");
        tutor.setId(tutorId);

        Subject subject = new Subject("Math", "Science");
        subject.setId(subjectId);
        tutor.getSubjects().add(subject);

        BookingDetailResponse expectedResponse = new BookingDetailResponse();

        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorService.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);
        when(availabilityService.isAvailable(anyString(), any(ZonedDateTime.class),
                any(ZonedDateTime.class), any(ZoneId.class))).thenReturn(true);
        when(bookingRepository.findByTutorIdAndDateTimeRange(anyString(),
                any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Arrays.asList());
        when(dtoMapper.toBookingDetailResponse(any(Booking.class),
                any(Student.class), any(Tutor.class), isNull())).thenReturn(expectedResponse);

        // Act
        BookingDetailResponse result = bookingService.createBooking(request);

        // Assert
        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void createBooking_InsufficientBalance_ThrowsException() throws Exception {
        // Arrange
        String studentId = "student123";
        String tutorId = "tutor456";

        CreateBookingRequest request = new CreateBookingRequest();
        request.setStudentId(studentId);
        request.setTutorId(tutorId);
        request.setDurationHours(2);

        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        student.setBalance(50.0); // Insufficient balance

        Tutor tutor = new Tutor("Jane", "jane@email.com", "password", 50.0, "Math tutor");
        tutor.setId(tutorId);

        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorService.findById(tutorId)).thenReturn(tutor);

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> bookingService.createBooking(request));
    }

    @Test
    void getBookingDetails_Success() throws Exception {
        // Arrange
        String bookingId = "booking123";
        String studentId = "student123";
        String tutorId = "tutor456";

        Booking booking = new Booking(studentId, tutorId,
                new Subject("Math", "Science"), LocalDateTime.now(), 2, 50.0);
        booking.setId(bookingId);

        Student student = new Student("John", "john@email.com", "password");
        Tutor tutor = new Tutor("Jane", "jane@email.com", "password", 50.0, "Math tutor");
        Payment payment = new Payment(bookingId, 100.0);
        BookingDetailResponse expectedResponse = new BookingDetailResponse();

        when(bookingRepository.findById(bookingId)).thenReturn(booking);
        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorService.findById(tutorId)).thenReturn(tutor);
        when(paymentService.findById(bookingId)).thenReturn(payment);
        when(dtoMapper.toBookingDetailResponse(booking, student, tutor, payment))
                .thenReturn(expectedResponse);

        // Act
        BookingDetailResponse result = bookingService.getBookingDetails(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void confirmBooking_Success() throws Exception {
        // Arrange
        String bookingId = "booking123";
        String studentId = "student123";
        String tutorId = "tutor456";

        Booking booking = new Booking(studentId, tutorId,
                new Subject("Math", "Science"), LocalDateTime.now(), 2, 50.0);
        booking.setId(bookingId);
        booking.setStatus(Booking.BookingStatus.PENDING);

        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        Tutor tutor = new Tutor("Jane", "jane@email.com", "password", 50.0, "Math tutor");
        tutor.setId(tutorId);
        Payment payment = new Payment(bookingId, 100.0);
        BookingDetailResponse expectedResponse = new BookingDetailResponse();

        when(bookingRepository.findById(bookingId)).thenReturn(booking);
        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorService.findById(tutorId)).thenReturn(tutor);
        when(paymentService.processPayment(studentId, bookingId, 100.0)).thenReturn(payment);
        when(dtoMapper.toBookingDetailResponse(booking, student, tutor, payment))
                .thenReturn(expectedResponse);

        // Act
        BookingDetailResponse result = bookingService.confirmBooking(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        verify(bookingRepository).update(booking);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void cancelBooking_WithRefund_Success() throws Exception {
        // Arrange
        String bookingId = "booking123";
        String studentId = "student123";
        String tutorId = "tutor456";
        String paymentId = "payment123";

        Payment payment = new Payment(bookingId, 100.0);
        payment.setId(paymentId);

        Booking booking = new Booking(studentId, tutorId,
                new Subject("Math", "Science"), LocalDateTime.now(), 2, 50.0);
        booking.setId(bookingId);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPayment(payment);

        Student student = new Student("John", "john@email.com", "password");
        Tutor tutor = new Tutor("Jane", "jane@email.com", "password", 50.0, "Math tutor");

        when(bookingRepository.findById(bookingId)).thenReturn(booking);
        when(studentService.findById(studentId)).thenReturn(student);
        when(tutorService.findById(tutorId)).thenReturn(tutor);

        // Act
        bookingService.cancelBooking(bookingId);

        // Assert
        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
        verify(paymentService).refundPayment(paymentId);
        verify(bookingRepository).update(booking);
        verify(eventPublisher).publishEvent(any());
    }
}