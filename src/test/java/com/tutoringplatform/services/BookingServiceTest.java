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
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.user.student.exceptions.InsufficientBalanceException;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.exceptions.BookedTimeSlotException;
import com.tutoringplatform.booking.exceptions.TutorNotAvailableException;
import com.tutoringplatform.user.tutor.exceptions.TutorNotTeachingSubjectException;
import com.tutoringplatform.shared.dto.request.UpdateBookingRequest;

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
    private IStudentRepository studentRepository;
    @Mock
    private ITutorRepository tutorRepository;
    @Mock
    private SubjectService subjectService;
    @Mock
    private PaymentService paymentService;

    private BookingService bookingService;

    private final String studentId = "student123";
    private final String tutorId = "tutor123";
    private final String subjectId = "subject123";
    private final LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
    private final String bookingId = "booking123";
    private final int durationHours = 2;
    private final double hourlyRate = 50.0;
    private final Subject subject = new Subject("Math", "Science");

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(
                bookingRepository, subjectService, availabilityService,
                paymentService, dtoMapper,
                eventPublisher, studentRepository, tutorRepository);
    }

    @Test
    void createBooking_Success() throws Exception {
        // Arrange
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

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
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

        CreateBookingRequest request = new CreateBookingRequest();
        request.setStudentId(studentId);
        request.setTutorId(tutorId);
        request.setDurationHours(2);

        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        student.setBalance(50.0); // Insufficient balance

        Tutor tutor = new Tutor("Jane", "jane@email.com", "password", 50.0, "Math tutor");
        tutor.setId(tutorId);

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);

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
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
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
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
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
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);

        // Act
        bookingService.cancelBooking(bookingId);

        // Assert
        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
        verify(paymentService).refundPayment(paymentId);
        verify(bookingRepository).update(booking);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void createBooking_TutorNotTeachingSubject_ThrowsException() throws Exception {
        // Arrange
        CreateBookingRequest request = createValidBookingRequest();

        Student student = createStudentWithBalance(200.0);
        Tutor tutor = createTutor();
        Subject subject = new Subject("Physics", "Science");
        // Tutor doesn't have this subject

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);

        // Act & Assert
        assertThrows(TutorNotTeachingSubjectException.class,
                () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_TutorNotAvailable_ThrowsException() throws Exception {
        // Arrange
        CreateBookingRequest request = createValidBookingRequest();

        Student student = createStudentWithBalance(200.0);
        Tutor tutor = createTutorWithSubject();

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);
        when(availabilityService.isAvailable(anyString(), any(ZonedDateTime.class),
                any(ZonedDateTime.class), any(ZoneId.class))).thenReturn(false);

        // Act & Assert
        assertThrows(TutorNotAvailableException.class,
                () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_TimeSlotAlreadyBooked_ThrowsException() throws Exception {
        // Arrange
        CreateBookingRequest request = createValidBookingRequest();

        Student student = createStudentWithBalance(200.0);
        Tutor tutor = createTutorWithSubject();

        // Existing booking at the same time
        Booking existingBooking = new Booking("other-student", tutorId, subject, dateTime, 1, 50.0);

        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(subjectService.findById(subjectId)).thenReturn(subject);
        when(availabilityService.isAvailable(anyString(), any(ZonedDateTime.class),
                any(ZonedDateTime.class), any(ZoneId.class))).thenReturn(true);
        when(bookingRepository.findByTutorIdAndDateTimeRange(anyString(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(existingBooking));

        // Act & Assert
        assertThrows(BookedTimeSlotException.class,
                () -> bookingService.createBooking(request));
    }

    @Test
    void updateBooking_Success() throws Exception {
        // Arrange
        String bookingId = "booking123";
        UpdateBookingRequest request = new UpdateBookingRequest();
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2);
        request.setDateTime(newDateTime);
        request.setDurationHours(3);

        Booking booking = createPendingBooking();
        Student student = createStudentWithBalance(200.0);
        Tutor tutor = createTutor();
        BookingDetailResponse expectedResponse = new BookingDetailResponse();

        when(bookingRepository.findById(bookingId)).thenReturn(booking);
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);
        when(availabilityService.isAvailable(anyString(), any(ZonedDateTime.class),
                any(ZonedDateTime.class), any(ZoneId.class))).thenReturn(true);
        when(dtoMapper.toBookingDetailResponse(any(), any(), any(), any())).thenReturn(expectedResponse);

        // Act
        BookingDetailResponse result = bookingService.updateBooking(bookingId, request);

        // Assert
        assertNotNull(result);
        assertEquals(newDateTime, booking.getDateTime());
        assertEquals(3, booking.getDurationHours());
        assertEquals(150.0, booking.getTotalCost()); // 3 hours * 50.0 rate
        verify(bookingRepository).update(booking);
    }

    @Test
    void completeBooking_UpdatesTutorEarnings() throws Exception {
        // Arrange
        String bookingId = "booking123";
        Booking booking = createConfirmedBooking();
        Student student = createStudentWithBalance(0); // Already paid
        Tutor tutor = createTutor();
        tutor.setEarnings(500.0);

        when(bookingRepository.findById(bookingId)).thenReturn(booking);
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(tutorRepository.findById(tutorId)).thenReturn(tutor);

        // Act
        bookingService.completeBooking(bookingId);

        // Assert
        assertEquals(Booking.BookingStatus.COMPLETED, booking.getStatus());
        assertEquals(600.0, tutor.getEarnings()); // 500 + 100
        verify(tutorRepository).update(tutor);
        verify(eventPublisher).publishEvent(any());
    }

    // Helper methods
    private CreateBookingRequest createValidBookingRequest() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setStudentId(studentId);
        request.setTutorId(tutorId);
        request.setSubjectId(subjectId);
        request.setDateTime(dateTime);
        request.setDurationHours(durationHours);
        return request;
    }

    private Student createStudentWithBalance(double balance) {
        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        student.setBalance(balance);
        student.setTimeZone(ZoneId.of("America/New_York"));
        return student;
    }

    private Tutor createTutor() {
        Tutor tutor = new Tutor("Jane", "jane@email.com", "password", 50.0, "Math tutor");
        tutor.setId(tutorId);
        return tutor;
    }

    private Tutor createTutorWithSubject() {
        Tutor tutor = createTutor();
        tutor.getSubjects().add(subject);
        return tutor;
    }

    private Booking createPendingBooking() {
        Booking booking = new Booking(studentId, tutorId, subject, dateTime, durationHours, hourlyRate);
        booking.setId(bookingId);
        booking.setStatus(Booking.BookingStatus.PENDING);
        return booking;
    }

    private Booking createConfirmedBooking() {
        Booking booking = createPendingBooking();
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setTotalCost(100.0);
        return booking;
    }
}