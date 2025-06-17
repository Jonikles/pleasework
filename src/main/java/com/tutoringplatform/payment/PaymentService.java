package com.tutoringplatform.payment;

import java.util.Stack;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.payment.command.IPaymentCommand;
import com.tutoringplatform.payment.command.ProcessPaymentCommand;
import com.tutoringplatform.payment.command.RefundPaymentCommand;

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