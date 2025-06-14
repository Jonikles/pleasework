package com.tutoringplatform.observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookingLogger implements BookingObserver {
    private List<String> logs;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BookingLogger() {
        this.logs = new ArrayList<>();
    }

    @Override
    public void update(BookingEvent event) {
        String logEntry = String.format("[%s] Booking %s: %s - Student: %s, Tutor: %s, Subject: %s",
                LocalDateTime.now().format(formatter),
                event.getEventType(),
                event.getBooking().getId(),
                event.getStudent().getName(),
                event.getTutor().getName(),
                event.getBooking().getSubject().getName());

        logs.add(logEntry);
        System.out.println("LOG: " + logEntry);
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public void printAllLogs() {
        System.out.println("\n=== BOOKING LOGS ===");
        for (String log : logs) {
            System.out.println(log);
        }
        System.out.println("==================\n");
    }
}