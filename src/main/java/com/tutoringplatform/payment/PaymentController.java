package com.tutoringplatform.payment;

import com.tutoringplatform.shared.dto.response.PaymentResponse;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;
import com.tutoringplatform.shared.dto.response.PaymentHistoryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Get payment details
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId)
            throws PaymentNotFoundException {
        logger.debug("Fetching payment details for ID: {}", paymentId);
        PaymentResponse payment = paymentService.getPaymentDetails(paymentId);
        return ResponseEntity.ok(payment);
    }

    // Get payment history for a student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<PaymentHistoryResponse> getStudentPaymentHistory(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.debug("Fetching payment history for student: {}", studentId);
        PaymentHistoryResponse history = paymentService.getStudentPaymentHistory(studentId, page, size);
        return ResponseEntity.ok(history);
    }

    // Get earnings history for a tutor
    @GetMapping("/tutor/{tutorId}/earnings")
    public ResponseEntity<PaymentHistoryResponse> getTutorEarnings(
            @PathVariable String tutorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.debug("Fetching earnings history for tutor: {}", tutorId);
        PaymentHistoryResponse earnings = paymentService.getTutorEarnings(tutorId, page, size);
        return ResponseEntity.ok(earnings);
    }
}