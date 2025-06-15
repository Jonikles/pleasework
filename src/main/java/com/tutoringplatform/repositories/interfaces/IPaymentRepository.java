package com.tutoringplatform.repositories.interfaces;

import java.util.List;
import com.tutoringplatform.models.Payment;

public interface IPaymentRepository extends IRepository<Payment> {
    Payment findByBookingId(String bookingId);
}