package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.response.PaymentHistoryResponse;
import com.tutoringplatform.dto.response.TransactionRecord;
import com.tutoringplatform.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /*@GetMapping("/history/{studentId}")
    public ResponseEntity<?> getPaymentHistory(@PathVariable String studentId) {
        try {
            PaymentHistoryResponse history = paymentService.getStudentPaymentHistory(studentId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }*/

}