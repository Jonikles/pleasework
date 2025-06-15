package com.tutoringplatform.observer;

import com.tutoringplatform.models.Booking;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Tutor;
import org.springframework.context.ApplicationEvent;

public class BookingEvent extends ApplicationEvent{
    private EventType eventType;
    private Booking booking;
    private Student student;
    private Tutor tutor;

    public enum EventType {
        CREATED, CONFIRMED, CANCELLED, COMPLETED
    }

    public BookingEvent(Object source, EventType eventType, Booking booking, Student student, Tutor tutor) {
        super(source);
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