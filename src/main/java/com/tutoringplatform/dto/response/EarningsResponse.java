// FILE: src/main/java/com/tutoringplatform/dto/response/EarningsResponse.java
package com.tutoringplatform.dto.response;

public class EarningsResponse {
    private double earnings;

    public EarningsResponse(double earnings) {
        this.earnings = earnings;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }
}