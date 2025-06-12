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
import java.util.Stack;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IStudentRepository studentRepository;

    @Mock
    private IBookingRepository bookingRepository;

    private PaymentService paymentService;
    private Student testStudent;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();

        ReflectionTestUtils.setField(paymentService, "paymentRepository", paymentRepository);
        ReflectionTestUtils.setField(paymentService, "studentRepository", studentRepository);
        ReflectionTestUtils.setField(paymentService, "bookingRepository", bookingRepository);

        ReflectionTestUtils.setField(paymentService, "commandHistory", new Stack<IPaymentCommand>());

        testStudent = new Student("John Doe", "john@student.com", "password123");
        testStudent.setBalance(200.0);

        Subject subject = new Subject("Math", "Mathematics");
        testBooking = new Booking("student123", "tutor456", subject,
                LocalDateTime.now().plusDays(1), 2, 50.0);
    }

    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPayment() throws Exception {
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        Payment payment = paymentService.processPayment("student123", "booking123", 100.0);

        assertNotNull(payment);
        assertEquals(100.0, payment.getAmount());
        assertEquals("booking123", payment.getBookingId());
        assertEquals(Payment.PaymentStatus.COMPLETED, payment.getStatus());
        assertEquals(100.0, testStudent.getBalance());
        verify(paymentRepository).save(any(Payment.class));
        verify(studentRepository).update(testStudent);
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void testProcessPaymentStudentNotFound() {
        when(studentRepository.findById("invalid")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            paymentService.processPayment("invalid", "booking123", 100.0);
        }, "Student not found");
    }

    @Test
    @DisplayName("Should throw exception for insufficient funds")
    void testProcessPaymentInsufficientFunds() {
        testStudent.setBalance(50.0);
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        assertThrows(Exception.class, () -> {
            paymentService.processPayment("student123", "booking123", 100.0);
        }, "Insufficient funds");
    }

    @Test
    @DisplayName("Should refund payment successfully")
    void testRefundPayment() throws Exception {
        Payment payment = new Payment("booking123", 100.0);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        when(paymentRepository.findById("payment123")).thenReturn(payment);
        when(bookingRepository.findById("booking123")).thenReturn(testBooking);
        when(studentRepository.findById("student123")).thenReturn(testStudent);
        testStudent.setBalance(100.0);

        paymentService.refundPayment("payment123");

        assertEquals(Payment.PaymentStatus.REFUNDED, payment.getStatus());
        assertEquals(200.0, testStudent.getBalance());
        verify(paymentRepository).update(payment);
        verify(studentRepository).update(testStudent);
    }

    @Test
    @DisplayName("Should not refund non-completed payment")
    void testRefundNonCompletedPayment() {
        Payment payment = new Payment("booking123", 100.0);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        when(paymentRepository.findById("payment123")).thenReturn(payment);
        when(bookingRepository.findById("booking123")).thenReturn(testBooking);
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        assertThrows(Exception.class, () -> {
            paymentService.refundPayment("payment123");
        }, "Can only refund completed payments");
    }

    @Test
    @DisplayName("Should undo last payment action")
    void testUndoLastPaymentAction() throws Exception {
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        Payment payment = paymentService.processPayment("student123", "booking123", 100.0);
        assertEquals(100.0, testStudent.getBalance());

        paymentService.undoLastPaymentAction();

        assertEquals(200.0, testStudent.getBalance());
        assertEquals(Payment.PaymentStatus.REFUNDED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(paymentRepository, times(1)).update(payment);
        verify(studentRepository, times(2)).update(testStudent);
    }

    @Test
    @DisplayName("Should throw exception when no action to undo")
    void testUndoWithEmptyHistory() {
        assertThrows(Exception.class, () -> {
            paymentService.undoLastPaymentAction();
        }, "No payment action to undo");
    }

    @Test
    @DisplayName("Should find payment by ID")
    void testFindById() throws Exception {
        Payment payment = new Payment("booking123", 100.0);
        when(paymentRepository.findById("payment123")).thenReturn(payment);

        Payment found = paymentService.findById("payment123");

        assertNotNull(found);
        assertEquals("booking123", found.getBookingId());
        assertEquals(100.0, found.getAmount());
    }

    @Test
    @DisplayName("Should throw exception when payment not found")
    void testFindByIdNotFound() {
        when(paymentRepository.findById("invalid")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            paymentService.findById("invalid");
        }, "Payment not found");
    }

    @Test
    @DisplayName("Should handle multiple undo operations")
    void testMultipleUndoOperations() throws Exception {
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        Payment payment1 = paymentService.processPayment("student123", "booking1", 50.0);
        assertEquals(150.0, testStudent.getBalance());

        Payment payment2 = paymentService.processPayment("student123", "booking2", 30.0);
        assertEquals(120.0, testStudent.getBalance());

        paymentService.undoLastPaymentAction();
        assertEquals(150.0, testStudent.getBalance());

        paymentService.undoLastPaymentAction();
        assertEquals(200.0, testStudent.getBalance());

        assertEquals(Payment.PaymentStatus.REFUNDED, payment1.getStatus());
        assertEquals(Payment.PaymentStatus.REFUNDED, payment2.getStatus());
    }
}