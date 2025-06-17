package com.tutoringplatform.payment.command;

import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.payment.IPaymentRepository;
import com.tutoringplatform.user.student.IStudentRepository;

public class ProcessPaymentCommand implements IPaymentCommand {
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
    public void execute() throws Exception {
        Student student = studentRepository.findById(this.student.getId());
        student.setBalance(student.getBalance() - amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
        studentRepository.update(student);
    }

    @Override
    public void undo() throws Exception {
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