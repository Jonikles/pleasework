package com.tutoringplatform.util;

import com.tutoringplatform.models.*;
import com.tutoringplatform.services.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataInitializer {

    private static List<Subject> subjects = new ArrayList<>();
    private static List<Student> students = new ArrayList<>();
    private static List<Tutor> tutors = new ArrayList<>();

    public static void initializeSubjects(SubjectService subjectService) {
        try {
            // Math subjects
            subjects.add(subjectService.createSubject("Algebra", "Mathematics"));
            subjects.add(subjectService.createSubject("Calculus", "Mathematics"));
            subjects.add(subjectService.createSubject("Geometry", "Mathematics"));
            subjects.add(subjectService.createSubject("Statistics", "Mathematics"));

            // Science subjects
            subjects.add(subjectService.createSubject("Physics", "Science"));
            subjects.add(subjectService.createSubject("Chemistry", "Science"));
            subjects.add(subjectService.createSubject("Biology", "Science"));

            // Language subjects
            subjects.add(subjectService.createSubject("English", "Language"));
            subjects.add(subjectService.createSubject("Spanish", "Language"));
            subjects.add(subjectService.createSubject("French", "Language"));

            // Programming subjects
            subjects.add(subjectService.createSubject("Java", "Programming"));
            subjects.add(subjectService.createSubject("Python", "Programming"));
            subjects.add(subjectService.createSubject("JavaScript", "Programming"));

            System.out.println("Subjects initialized: " + subjects.size() + " subjects created");
        } catch (Exception e) {
            System.err.println("Error initializing subjects: " + e.getMessage());
        }
    }

    public static void initializeTutors(TutorService tutorService, SubjectService subjectService) {
        try {
            // Tutor 1
            Tutor mathTutor = new Tutor("Dr. Sarah Johnson", "sarah@tutor.com", "password123",
                    75.0, "PhD in Mathematics with 15 years teaching experience");
            tutorService.register(mathTutor);

            mathTutor.addSubject(subjectService.findByName("Algebra"));
            mathTutor.addSubject(subjectService.findByName("Calculus"));
            mathTutor.addSubject(subjectService.findByName("Geometry"));

            String[] weekdays = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
            for (String day : weekdays) {
                for (int hour = 14; hour <= 20; hour++) {
                    mathTutor.addAvailability(day, hour);
                }
            }
            tutorService.update(mathTutor);
            tutors.add(mathTutor);

            // Tutor 2
            Tutor scienceTutor = new Tutor("Prof. Michael Chen", "michael@tutor.com", "password123",
                    65.0, "Master's in Physics, specializing in high school sciences");
            tutorService.register(scienceTutor);

            scienceTutor.addSubject(subjectService.findByName("Physics"));
            scienceTutor.addSubject(subjectService.findByName("Chemistry"));

            for (String day : weekdays) {
                for (int hour = 17; hour <= 21; hour++) {
                    scienceTutor.addAvailability(day, hour);
                }
            }
            for (int hour = 10; hour <= 18; hour++) {
                scienceTutor.addAvailability("Saturday", hour);
                scienceTutor.addAvailability("Sunday", hour);
            }
            tutorService.update(scienceTutor);
            tutors.add(scienceTutor);

            // Tutor 3
            Tutor languageTutor = new Tutor("Maria Rodriguez", "maria@tutor.com", "password123",
                    55.0, "Native Spanish speaker, certified ESL instructor");
            tutorService.register(languageTutor);

            languageTutor.addSubject(subjectService.findByName("English"));
            languageTutor.addSubject(subjectService.findByName("Spanish"));
            languageTutor.addSubject(subjectService.findByName("French"));

            for (String day : weekdays) {
                for (int hour = 8; hour <= 14; hour++) {
                    languageTutor.addAvailability(day, hour);
                }
            }
            tutorService.update(languageTutor);
            tutors.add(languageTutor);

            // Tutor 4
            Tutor codingTutor = new Tutor("Alex Kumar", "alex@tutor.com", "password123",
                    85.0, "Senior Software Engineer, 10+ years industry experience");
            tutorService.register(codingTutor);

            codingTutor.addSubject(subjectService.findByName("Java"));
            codingTutor.addSubject(subjectService.findByName("Python"));
            codingTutor.addSubject(subjectService.findByName("JavaScript"));

            String[] allDays = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
            for (String day : allDays) {
                for (int hour = 18; hour <= 22; hour++) {
                    codingTutor.addAvailability(day, hour);
                }
            }
            tutorService.update(codingTutor);
            tutors.add(codingTutor);

            System.out.println("Tutors initialized: " + tutors.size() + " tutors created");
        } catch (Exception e) {
            System.err.println("Error initializing tutors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initializeStudents(StudentService studentService) {
        try {
            // Student 1
            Student student1 = new Student("Emma Wilson", "emma@student.com", "password123");
            studentService.register(student1);
            student1.addFunds(500.0);
            studentService.update(student1);
            students.add(student1);

            // Student 2
            Student student2 = new Student("James Brown", "james@student.com", "password123");
            studentService.register(student2);
            student2.addFunds(750.0);
            studentService.update(student2);
            students.add(student2);

            // Student 3
            Student student3 = new Student("Linda Davis", "linda@student.com", "password123");
            studentService.register(student3);
            student3.addFunds(1000.0);
            studentService.update(student3);
            students.add(student3);

            System.out.println("Students initialized: " + students.size() + " students created");
        } catch (Exception e) {
            System.err.println("Error initializing students: " + e.getMessage());
        }
    }

    public static void initializeBookings(BookingService bookingService, PaymentService paymentService,
            SubjectService subjectService) {
        try {
            if (students.isEmpty() || tutors.isEmpty()) {
                System.err.println("Cannot create bookings: No students or tutors available");
                return;
            }

            // Booking 1
            LocalDateTime pastDate = LocalDateTime.now().minusDays(5).withHour(15).withMinute(0);
            Booking booking1 = bookingService.createBooking(
                    students.get(0).getId(),
                    tutors.get(0).getId(),
                    subjectService.findByName("Calculus"),
                    pastDate,
                    2);

            Payment payment1 = paymentService.processPayment(students.get(0).getId(),
                    booking1.getId(),
                    booking1.getTotalCost());
            bookingService.confirmBooking(booking1.getId(), payment1);
            bookingService.completeBooking(booking1.getId());

            // Booking 2
            LocalDateTime futureDate = LocalDateTime.now().plusDays(3).withHour(18).withMinute(0);
            Booking booking2 = bookingService.createBooking(
                    students.get(1).getId(),
                    tutors.get(3).getId(),
                    subjectService.findByName("Python"),
                    futureDate,
                    1);

            Payment payment2 = paymentService.processPayment(students.get(1).getId(),
                    booking2.getId(),
                    booking2.getTotalCost());
            bookingService.confirmBooking(booking2.getId(), payment2);

            System.out.println("Bookings initialized: Created bookings in various states");
        } catch (Exception e) {
            System.err.println("Error initializing bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initializeReviews(ReviewService reviewService, BookingService bookingService) {
        try {
            List<Booking> allBookings = new ArrayList<>();
            for (Student student : students) {
                allBookings.addAll(bookingService.findByStudent(student.getId()));
            }

            for (Booking booking : allBookings) {
                if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
                    if (booking.getStudentId().equals(students.get(0).getId())) {
                        reviewService.createReview(booking.getId(), 5,
                                "Excellent tutor! Very patient and explains concepts clearly. Highly recommended!");
                    }
                }
            }

            System.out.println("Reviews initialized for completed bookings");
        } catch (Exception e) {
            System.err.println("Error initializing reviews: " + e.getMessage());
        }
    }

    public static void initializeAllData(SubjectService subjectService, TutorService tutorService,
            StudentService studentService, BookingService bookingService,
            PaymentService paymentService, ReviewService reviewService) {
        System.out.println("\nStarting Data Initialization");

        initializeSubjects(subjectService);
        initializeTutors(tutorService, subjectService);
        initializeStudents(studentService);
        initializeBookings(bookingService, paymentService, subjectService);
        initializeReviews(reviewService, bookingService);

        System.out.println("\nData Initialization Complete");
        System.out.println("\nTest Accounts:");
        System.out.println("Students: emma@student.com, james@student.com, linda@student.com");
        System.out.println("Tutors: sarah@tutor.com, michael@tutor.com, maria@tutor.com, alex@tutor.com");
        System.out.println("Password for all: password123\n");
    }
}