package com.tutoringplatform.payment;

import com.tutoringplatform.shared.IRepository;

public interface IPaymentRepository extends IRepository<Payment> {
    Payment findByBookingId(String bookingId);
}