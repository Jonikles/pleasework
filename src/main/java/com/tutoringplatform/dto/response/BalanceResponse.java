// FILE: src/main/java/com/tutoringplatform/dto/response/BalanceResponse.java
package com.tutoringplatform.dto.response;

public class BalanceResponse {
    private double balance;

    public BalanceResponse(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}