package com.tutoringplatform.user.student;

import com.tutoringplatform.user.User;
import com.tutoringplatform.user.UserType;

public class Student extends User {
    private double balance;

    public Student(String name, String email, String password) {
        super(name, email, password, UserType.STUDENT);
        this.balance = 0.0;
    }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}