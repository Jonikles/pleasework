// FILE: src/main/java/com/tutoringplatform/dto/response/StudentResponse.java
package com.tutoringplatform.dto.response;

import com.tutoringplatform.models.UserType;

public class StudentResponse {
    private String id;
    private String name;
    private String email;
    private String userType = UserType.STUDENT.getDisplayName();
    private double balance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}