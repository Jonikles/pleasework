package com.tutoringplatform.command;


import com.tutoringplatform.models.Payment;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IPaymentCommand;
import com.tutoringplatform.repositories.interfaces.IPaymentRepository;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;

public class RefundPaymentCommand implements IPaymentCommand {
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
    public void execute() throws Exception {
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new Exception("Can only refund completed payments");
        }
        student.addFunds(amount);
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        paymentRepository.update(payment);
        studentRepository.update(student);
    }

    @Override
    public void undo() throws Exception {
        student.deductFunds(amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        paymentRepository.update(payment);
        studentRepository.update(student);
    }

    @Override
    public Payment getPayment() {
        return payment;
    }
}