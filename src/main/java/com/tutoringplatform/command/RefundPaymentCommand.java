package com.tutoringplatform.command;


import com.tutoringplatform.models.Payment;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IPaymentCommand;

public class RefundPaymentCommand implements IPaymentCommand {
    private Payment payment;
    private Student student;
    private double amount;

    public RefundPaymentCommand(Payment payment, Student student, double amount) {
        this.payment = payment;
        this.student = student;
        this.amount = amount;
    }

    @Override
    public void execute() throws Exception {
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new Exception("Can only refund completed payments");
        }
        student.setBalance(student.getBalance() + amount);
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
    }

    @Override
    public void undo() throws Exception {
        student.setBalance(student.getBalance() - amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
    }

    @Override
    public Payment getPayment() {
        return payment;
    }
}