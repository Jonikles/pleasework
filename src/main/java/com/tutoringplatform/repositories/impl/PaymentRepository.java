package com.tutoringplatform.repositories.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tutoringplatform.models.Payment;
import com.tutoringplatform.repositories.interfaces.IPaymentRepository;

@Repository
public class PaymentRepository implements IPaymentRepository {
    private Map<String, Payment> payments = new HashMap<>();

    @Override
    public Payment findById(String id) {
        return payments.get(id);
    }

    @Override
    public Payment findByBookingId(String bookingId) {
        return payments.values().stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    @Override
    public void save(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    @Override
    public void update(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    @Override
    public void delete(String id) {
        payments.remove(id);
    }
}