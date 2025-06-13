package com.tutoringplatform.config;

import com.tutoringplatform.services.BookingService;
import com.tutoringplatform.observer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class BookingConfiguration {

    @Autowired
    private BookingService bookingService;

    @PostConstruct
    public void setupObservers() {
        BookingLogger bookingLogger = new BookingLogger();

        bookingService.addObserver(bookingLogger);

        System.out.println("Booking observers configured: Logger and TutorUpdater registered");
    }
}