package com.tutoringplatform.repositories.interfaces;

import com.tutoringplatform.models.Payment;

public interface IPaymentCommand {
    void execute() throws Exception;
    void undo() throws Exception;
    Payment getPayment();
}