package com.tutoringplatform.command;

import com.tutoringplatform.models.Payment;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IPaymentCommand;

public class ProcessPaymentCommand implements IPaymentCommand {
    private Payment payment;
    private Student student;
    private double amount;

    public ProcessPaymentCommand(Payment payment, Student student, double amount) {
        this.payment = payment;
        this.student = student;
        this.amount = amount;
    }

    @Override
    public void execute() throws Exception {
        student.setBalance(student.getBalance() - amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
    }

    @Override
    public void undo() throws Exception {
        student.setBalance(student.getBalance() + amount);
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
    }

    @Override
    public Payment getPayment() {
        return payment;
    }
}