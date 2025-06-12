package com.tutoringplatform.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected UserType userType;
    protected List<Booking> bookings;

    public enum UserType {
        STUDENT, TUTOR
    }

    public User(String name, String email, String password, UserType userType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.bookings = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

}