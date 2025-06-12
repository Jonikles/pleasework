package com.tutoringplatform.observer;

import com.tutoringplatform.models.Booking;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;

public class BookingEvent {
    private EventType eventType;
    private Booking booking;
    private Student student;
    private Tutor tutor;

    public enum EventType {
        CREATED, CONFIRMED, CANCELLED, COMPLETED
    }

    public BookingEvent(EventType eventType, Booking booking, Student student, Tutor tutor) {
        this.eventType = eventType;
        this.booking = booking;
        this.student = student;
        this.tutor = tutor;
    }

    public EventType getEventType() { return eventType; }

    public Booking getBooking() { return booking; }

    public Student getStudent() { return student; }

    public Tutor getTutor() { return tutor; }
}