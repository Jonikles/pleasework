package com.tutoringplatform.config;

import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.payment.PaymentService;
import com.tutoringplatform.review.ReviewService;
import com.tutoringplatform.user.student.StudentService;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.user.tutor.TutorService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataInitializationConfiguration {

    @Bean
    @SuppressWarnings("unused")
    CommandLineRunner init(SubjectService subjectService, TutorService tutorService, StudentService studentService,
            BookingService bookingService, PaymentService paymentService, ReviewService reviewService) {
        return args -> {
            System.out.println("Application started! Access at http://localhost:8080");
        };
    }
}