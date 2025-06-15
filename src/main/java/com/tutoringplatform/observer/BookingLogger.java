package com.tutoringplatform.observer;

//import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookingLogger {
    private List<String> logs;
    //private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BookingLogger() {
        this.logs = new ArrayList<>();
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