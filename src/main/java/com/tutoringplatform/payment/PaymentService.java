package com.tutoringplatform.payment;

import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.exceptions.BookingNotFoundException;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.exceptions.InsufficientBalanceException;
import com.tutoringplatform.payment.command.IPaymentCommand;
import com.tutoringplatform.payment.command.ProcessPaymentCommand;
import com.tutoringplatform.payment.command.RefundPaymentCommand;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

@Service
public class PaymentService {
    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final IPaymentRepository paymentRepository;
    private final IStudentRepository studentRepository;
    private final IBookingRepository bookingRepository;
    private final Stack<IPaymentCommand> commandHistory;
    @Autowired
    public PaymentService(IPaymentRepository paymentRepository, IStudentRepository studentRepository,
            IBookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.bookingRepository = bookingRepository;
        this.commandHistory = new Stack<>();
    }

    @Transactional
    public Payment processPayment(String studentId, String bookingId, double amount) throws InsufficientBalanceException {
        logger.info("Processing payment for student {}, booking {}, amount {}", studentId, bookingId, amount);

        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new IllegalStateException("Data corruption error: Student not found for payment");
        }

        if (student.getBalance() < amount) {
            logger.warn("Insufficient balance for student {}. Required: {}, Available: {}",
                studentId, amount, student.getBalance());
            throw new InsufficientBalanceException(studentId, amount, student.getBalance());
        }

        Payment payment = new Payment(bookingId, amount);

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                payment, student, amount, paymentRepository, studentRepository);

        command.execute();
        commandHistory.push(command);

        logger.info("Payment processed successfully. Payment ID: {}, amount: {}", payment.getId(), amount);

        return payment;
    }

    @Transactional
    public void refundPayment(String paymentId) throws BookingNotFoundException, PaymentNotFoundException {
        logger.info("Refunding payment {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            logger.error("Payment not found for refund");
            throw new PaymentNotFoundException(paymentId);
        }

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            logger.error("Attempted to refund a payment that is not completed. Status: {}", payment.getStatus());
            throw new IllegalStateException(
                    "Can only refund completed payments. Current status: " + payment.getStatus());
        }

        Booking booking = bookingRepository.findById(payment.getBookingId());
        if (booking == null) {
            logger.error("Booking not found for refund");
            throw new BookingNotFoundException(payment.getBookingId());
        }

        Student student = studentRepository.findById(booking.getStudentId());
        if (student == null) {
            logger.error("Student not found for refund");
            throw new IllegalStateException("Data corruption error: Student not found for refund");
        }

        RefundPaymentCommand command = new RefundPaymentCommand(
                payment, student, payment.getAmount(), paymentRepository, studentRepository);

        command.execute();
        commandHistory.push(command);

        logger.info("Payment refunded successfully. Payment ID: {}, amount: {}", payment.getId(), payment.getAmount());
    }

    public Payment findById(String id) {
        if (id == null)
            return null;
        return paymentRepository.findById(id);
    }
}