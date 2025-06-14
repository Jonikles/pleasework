package com.tutoringplatform.services;

import java.util.Stack;

import com.tutoringplatform.command.ProcessPaymentCommand;
import com.tutoringplatform.command.RefundPaymentCommand;
import com.tutoringplatform.models.Booking;
import com.tutoringplatform.models.Payment;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IBookingRepository;
import com.tutoringplatform.repositories.interfaces.IPaymentCommand;
import com.tutoringplatform.repositories.interfaces.IPaymentRepository;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
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
    public Payment processPayment(String studentId, String bookingId, double amount) throws Exception {
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new Exception("Student not found");
        }

        Payment payment = new Payment(bookingId, amount);

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                payment, student, amount, paymentRepository, studentRepository);

        command.execute();
        commandHistory.push(command);

        return payment;
    }

    @Transactional
    public void refundPayment(String paymentId) throws Exception {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new Exception("Payment not found");
        }

        Booking booking = bookingRepository.findById(payment.getBookingId());
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        if (student == null) {
            throw new Exception("Student not found");
        }

        RefundPaymentCommand command = new RefundPaymentCommand(
                payment, student, payment.getAmount(), paymentRepository, studentRepository);

        command.execute();
        commandHistory.push(command);
    }

    public void undoLastPaymentAction() throws Exception {
        if (commandHistory.isEmpty()) {
            throw new Exception("No payment action to undo");
        }

        IPaymentCommand lastCommand = commandHistory.pop();
        lastCommand.undo();
    }

    public Payment findById(String id) throws Exception {
        Payment payment = paymentRepository.findById(id);
        if (payment == null) {
            throw new Exception("Payment not found");
        }
        return payment;
    }
}