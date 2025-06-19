package com.tutoringplatform.payment.command;

import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.payment.IPaymentRepository;
import com.tutoringplatform.user.student.IStudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefundPaymentCommand implements IPaymentCommand {
    private final Logger logger = LoggerFactory.getLogger(RefundPaymentCommand.class);
    private Payment payment;
    private Student student;
    private double amount;
    private IPaymentRepository paymentRepository;
    private IStudentRepository studentRepository;

    public RefundPaymentCommand(Payment payment, Student student, double amount, IPaymentRepository paymentRepository, IStudentRepository studentRepository) {
        this.payment = payment;
        this.student = student;
        this.amount = amount;
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void execute() {
        logger.info("Executing refund payment command for student {}, amount {}", student.getId(), amount);

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            logger.error("Can only refund completed payments");
            throw new IllegalStateException("Data corruption error: Can only refund completed payments");
        }
        student.setBalance(student.getBalance() + amount);
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        paymentRepository.update(payment);
        studentRepository.update(student);
        logger.info("Refund payment command for student {} completed successfully.", student.getId());
    }

    @Override
    public void undo() {
        logger.info("Undoing refund payment command for student {}, amount {}", student.getId(), amount);
        student.setBalance(student.getBalance() - amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        paymentRepository.update(payment);
        studentRepository.update(student);
    }

    @Override
    public Payment getPayment() {
        return payment;
    }
}