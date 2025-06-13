package com.tutoringplatform.models;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<Review> reviewsGiven;
    private double balance;

    public Student(String name, String email, String password) {
        super(name, email, password, UserType.STUDENT);
        this.reviewsGiven = new ArrayList<>();
        this.balance = 0.0;
    }


    public List<Review> getReviewsGiven() {
        return reviewsGiven;
    }

    public void addReview(Review review) {
        reviewsGiven.add(review);
    }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}