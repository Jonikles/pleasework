package com.tutoringplatform.dto.response.info;

import java.time.LocalDateTime;

public class PaymentInfo {
    private String status;
    private LocalDateTime paidAt;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}