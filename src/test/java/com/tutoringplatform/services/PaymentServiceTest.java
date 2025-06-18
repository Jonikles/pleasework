package com.tutoringplatform.services;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.student.exceptions.InsufficientBalanceException;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;
import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.payment.PaymentService;
import com.tutoringplatform.payment.IPaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private IPaymentRepository paymentRepository;
    @Mock
    private IStudentRepository studentRepository;
    @Mock
    private IBookingRepository bookingRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, studentRepository, bookingRepository);
    }

@Test
    void processPayment_Success() throws Exception {
        // Arrange
        String studentId = "student123";
        String bookingId = "booking456";
        double amount = 100.0;

        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        student.setBalance(200.0);

        when(studentRepository.findById(studentId)).thenReturn(student);

        // Act
        Payment result = paymentService.processPayment(studentId, bookingId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(bookingId, result.getBookingId());
        assertEquals(amount, result.getAmount());
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
        verify(studentRepository).update(student);
        assertEquals(100.0, student.getBalance()); // 200 - 100
    }

    @Test
    void processPayment_InsufficientBalance_ThrowsException() throws Exception {
        // Arrange
        String studentId = "student123";
        String bookingId = "booking456";
        double amount = 100.0;

        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        student.setBalance(50.0); // Insufficient

        when(studentRepository.findById(studentId)).thenReturn(student);

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> paymentService.processPayment(studentId, bookingId, amount));

        verify(paymentRepository, never()).save(any());
        verify(studentRepository, never()).update(any());
    }

    @Test
    void refundPayment_Success() throws Exception {
        // Arrange
        String paymentId = "payment123";
        String bookingId = "booking456";
        String studentId = "student789";
        double amount = 100.0;

        Payment payment = new Payment(bookingId, amount);
        payment.setId(paymentId);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        Booking booking = mock(Booking.class);
        when(booking.getStudentId()).thenReturn(studentId);

        Student student = new Student("John", "john@email.com", "password");
        student.setId(studentId);
        student.setBalance(50.0);

        when(paymentRepository.findById(paymentId)).thenReturn(payment);
        when(bookingRepository.findById(bookingId)).thenReturn(booking);
        when(studentRepository.findById(studentId)).thenReturn(student);

        // Act
        paymentService.refundPayment(paymentId);

        // Assert
        assertEquals(Payment.PaymentStatus.REFUNDED, payment.getStatus());
        assertEquals(150.0, student.getBalance()); // 50 + 100
        verify(paymentRepository).update(payment);
        verify(studentRepository).update(student);
    }

    @Test
    void refundPayment_PaymentNotFound_ThrowsException() {
        // Arrange
        String paymentId = "nonexistent";
        when(paymentRepository.findById(paymentId)).thenReturn(null);

        // Act & Assert
        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.refundPayment(paymentId));
    }

    @Test
    void findById_Success() throws Exception {
        // Arrange
        String paymentId = "payment123";
        Payment payment = new Payment("booking123", 100.0);
        payment.setId(paymentId);

        when(paymentRepository.findById(paymentId)).thenReturn(payment);

        // Act
        Payment result = paymentService.findById(paymentId);

        // Assert
        assertNotNull(result);
        assertEquals(paymentId, result.getId());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        // Arrange
        String paymentId = "nonexistent";
        when(paymentRepository.findById(paymentId)).thenReturn(null);

        // Act & Assert
        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.findById(paymentId));
    }
}