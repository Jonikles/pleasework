package com.tutoringplatform.repositories.interfaces;

import java.util.List;

import com.tutoringplatform.models.Payment;

public interface IPaymentRepository {
    Payment findById(String id);

    Payment findByBookingId(String bookingId);

    List<Payment> findAll();

    void save(Payment payment);

    void update(Payment payment);

    void delete(String id);
}