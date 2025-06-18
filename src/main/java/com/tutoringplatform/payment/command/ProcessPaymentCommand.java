package com.tutoringplatform.payment.command;

import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.exceptions.InsufficientBalanceException;
import com.tutoringplatform.payment.IPaymentRepository;
import com.tutoringplatform.user.student.IStudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessPaymentCommand implements IPaymentCommand {
    private final Logger logger = LoggerFactory.getLogger(ProcessPaymentCommand.class);
    private Payment payment;
    private Student student;
    private double amount;
    private IPaymentRepository paymentRepository;
    private IStudentRepository studentRepository;

    public ProcessPaymentCommand(Payment payment, Student student, double amount, IPaymentRepository paymentRepository, IStudentRepository studentRepository) {
        this.payment = payment;
        this.student = student;
        this.amount = amount;
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void execute() throws InsufficientBalanceException {
        logger.info("Executing payment command for student {}, amount {}", student.getId(), amount);

        Student student = studentRepository.findById(this.student.getId());
        if (student == null) {
            logger.error("Student not found for payment command");
            throw new IllegalStateException("Data corruption error: Student not found for payment command");
        }

        if (student.getBalance() < amount) {
            logger.warn("Insufficient balance for student {}, amount {}, balance {}", student.getId(), amount, student.getBalance());
            throw new InsufficientBalanceException(student.getId(), amount, student.getBalance());
        }

        student.setBalance(student.getBalance() - amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        paymentRepository.save(payment);
        studentRepository.update(student);
        logger.info("Payment for student {} completed successfully.", student.getId());
    }

    @Override
    public void undo() throws InsufficientBalanceException {
        logger.info("Undoing payment command for student {}, amount {}", student.getId(), amount);
        student.setBalance(student.getBalance() + amount);
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        paymentRepository.update(payment);
        studentRepository.update(student);
    }

    @Override
    public Payment getPayment() {
        return payment;
    }
}