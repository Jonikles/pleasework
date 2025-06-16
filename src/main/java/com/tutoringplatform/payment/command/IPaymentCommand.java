package com.tutoringplatform.payment.command;

import com.tutoringplatform.payment.Payment;

public interface IPaymentCommand {
    void execute() throws Exception;
    void undo() throws Exception;
    Payment getPayment();
}