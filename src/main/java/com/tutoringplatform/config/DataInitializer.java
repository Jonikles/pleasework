package com.tutoringplatform.config;

import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.subject.ISubjectRepository;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.payment.IPaymentRepository;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.review.IReviewRepository;
import com.tutoringplatform.notification.NotificationService;
import com.tutoringplatform.notification.NotificationType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private ISubjectRepository subjectRepository;

    @Autowired
    private IStudentRepository studentRepository;

    @Autowired
    private ITutorRepository tutorRepository;

    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private IReviewRepository reviewRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");

        if (!subjectRepository.findAll().isEmpty()) {
            logger.info("Data already exists, skipping initialization");
            return;
        }

        try {
            initializeSubjects();
            initializeStudents();
            initializeTutors();
            assignSubjectsToTutors();
            setupTutorAvailability();
            createSampleBookings();
            createSampleReviews();
            createSampleNotifications();

            logger.info("Data initialization completed successfully!");
            printSummary();

        } catch (Exception e) {
            logger.error("Error during data initialization", e);
            throw e;
        }
    }

    private void initializeSubjects() {
        logger.info("Initializing subjects...");

        List<Subject> subjects = List.of(
                // STEM
                new Subject("Mathematics", "STEM"),
                new Subject("Physics", "STEM"),
                new Subject("Chemistry", "STEM"),
                new Subject("Biology", "STEM"),
                new Subject("Computer Science", "STEM"),
                new Subject("Statistics", "STEM"),
                new Subject("Calculus", "STEM"),
                new Subject("Algebra", "STEM"),

                // Languages
                new Subject("English", "Languages"),
                new Subject("Spanish", "Languages"),
                new Subject("French", "Languages"),
                new Subject("German", "Languages"),
                new Subject("Mandarin", "Languages"),
                new Subject("Japanese", "Languages"),

                // Humanities
                new Subject("History", "Humanities"),
                new Subject("Philosophy", "Humanities"),
                new Subject("Literature", "Humanities"),
                new Subject("Psychology", "Humanities"),
                new Subject("Sociology", "Humanities"),

                // Arts
                new Subject("Music Theory", "Arts"),
                new Subject("Piano", "Arts"),
                new Subject("Guitar", "Arts"),
                new Subject("Art History", "Arts"),
                new Subject("Drawing", "Arts"),

                // Business
                new Subject("Economics", "Business"),
                new Subject("Accounting", "Business"),
                new Subject("Marketing", "Business"),
                new Subject("Finance", "Business"),
                new Subject("Business Strategy", "Business"));

        subjects.forEach(subjectRepository::save);
        logger.info("Created {} subjects", subjects.size());
    }

    private void initializeStudents() {
        logger.info("Initializing students...");

        List<Student> students = List.of(
                createStudent("Alice Johnson", "alice.johnson@email.com", 500.0),
                createStudent("Bob Smith", "bob.smith@email.com", 750.0),
                createStudent("Carol Davis", "carol.davis@email.com", 300.0),
                createStudent("David Wilson", "david.wilson@email.com", 1000.0),
                createStudent("Emma Brown", "emma.brown@email.com", 650.0),
                createStudent("Frank Miller", "frank.miller@email.com", 200.0),
                createStudent("Grace Lee", "grace.lee@email.com", 850.0),
                createStudent("Henry Taylor", "henry.taylor@email.com", 400.0),
                createStudent("Isabel Garcia", "isabel.garcia@email.com", 600.0),
                createStudent("Jack Anderson", "jack.anderson@email.com", 950.0));

        students.forEach(studentRepository::save);
        logger.info("Created {} students", students.size());
    }

    private void initializeTutors() {
        logger.info("Initializing tutors...");

        List<Tutor> tutors = List.of(
                createTutor("Dr. Sarah Mitchell", "sarah.mitchell@email.com", 75.0,
                        "PhD in Mathematics with 10+ years of teaching experience. Specializing in calculus and algebra."),
                createTutor("Prof. Michael Chen", "michael.chen@email.com", 80.0,
                        "Computer Science professor with expertise in programming and algorithms. Industry experience at Google."),
                createTutor("Dr. Emily Rodriguez", "emily.rodriguez@email.com", 70.0,
                        "Chemistry PhD and former pharmaceutical researcher. Expert in organic and analytical chemistry."),
                createTutor("James Thompson", "james.thompson@email.com", 60.0,
                        "Professional musician and music teacher. 15 years experience teaching piano and music theory."),
                createTutor("Dr. Lisa Wang", "lisa.wang@email.com", 85.0,
                        "Physics professor with research background in quantum mechanics. Published author and conference speaker."),
                createTutor("Maria Santos", "maria.santos@email.com", 55.0,
                        "Native Spanish speaker with teaching certification. Specializes in conversational Spanish and grammar."),
                createTutor("Dr. Robert Kumar", "robert.kumar@email.com", 65.0,
                        "Economics PhD with Wall Street experience. Expert in microeconomics, macroeconomics, and finance."),
                createTutor("Jennifer Adams", "jennifer.adams@email.com", 50.0,
                        "English Literature graduate with creative writing background. Passionate about helping students with essays."),
                createTutor("Dr. Thomas Lee", "thomas.lee@email.com", 90.0,
                        "Biology professor and researcher. Specializes in molecular biology and genetics."),
                createTutor("Anna Kowalski", "anna.kowalski@email.com", 45.0,
                        "Art teacher with fine arts degree. Expert in drawing, painting, and art history."));

        tutors.forEach(tutorRepository::save);
        logger.info("Created {} tutors", tutors.size());
    }

    private void assignSubjectsToTutors() {
        logger.info("Assigning subjects to tutors...");

        List<Tutor> tutors = tutorRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll();

        // Dr. Sarah Mitchell - Math subjects
        assignSubjectsToTutor(tutors.get(0), getSubjectsByNames(subjects,
                "Mathematics", "Calculus", "Algebra", "Statistics"));

        // Prof. Michael Chen - Computer Science
        assignSubjectsToTutor(tutors.get(1), getSubjectsByNames(subjects,
                "Computer Science", "Mathematics", "Statistics"));

        // Dr. Emily Rodriguez - Chemistry and Biology
        assignSubjectsToTutor(tutors.get(2), getSubjectsByNames(subjects,
                "Chemistry", "Biology"));

        // James Thompson - Music
        assignSubjectsToTutor(tutors.get(3), getSubjectsByNames(subjects,
                "Music Theory", "Piano"));

        // Dr. Lisa Wang - Physics and Math
        assignSubjectsToTutor(tutors.get(4), getSubjectsByNames(subjects,
                "Physics", "Mathematics", "Calculus"));

        // Maria Santos - Spanish
        assignSubjectsToTutor(tutors.get(5), getSubjectsByNames(subjects,
                "Spanish", "English"));

        // Dr. Robert Kumar - Economics and Business
        assignSubjectsToTutor(tutors.get(6), getSubjectsByNames(subjects,
                "Economics", "Finance", "Business Strategy", "Accounting"));

        // Jennifer Adams - English and Literature
        assignSubjectsToTutor(tutors.get(7), getSubjectsByNames(subjects,
                "English", "Literature", "History"));

        // Dr. Thomas Lee - Biology
        assignSubjectsToTutor(tutors.get(8), getSubjectsByNames(subjects,
                "Biology", "Chemistry"));

        // Anna Kowalski - Arts
        assignSubjectsToTutor(tutors.get(9), getSubjectsByNames(subjects,
                "Drawing", "Art History"));

        logger.info("Assigned subjects to all tutors");
    }

    private void setupTutorAvailability() {
        logger.info("Setting up tutor availability...");

        List<Tutor> tutors = tutorRepository.findAll();

        for (Tutor tutor : tutors) {
            try {
                // Add weekday availability (9 AM - 5 PM)
                availabilityService.addRecurringAvailability(tutor.getId(),
                        DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
                availabilityService.addRecurringAvailability(tutor.getId(),
                        DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
                availabilityService.addRecurringAvailability(tutor.getId(),
                        DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
                availabilityService.addRecurringAvailability(tutor.getId(),
                        DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
                availabilityService.addRecurringAvailability(tutor.getId(),
                        DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

                // Some tutors also available on weekends
                if (tutors.indexOf(tutor) % 3 == 0) {
                    availabilityService.addRecurringAvailability(tutor.getId(),
                            DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(16, 0));
                }

                // Some tutors have evening hours
                if (tutors.indexOf(tutor) % 2 == 0) {
                    availabilityService.addRecurringAvailability(tutor.getId(),
                            DayOfWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(21, 0));
                    availabilityService.addRecurringAvailability(tutor.getId(),
                            DayOfWeek.WEDNESDAY, LocalTime.of(18, 0), LocalTime.of(21, 0));
                }

            } catch (Exception e) {
                logger.warn("Failed to set availability for tutor {}: {}", tutor.getId(), e.getMessage());
            }
        }

        logger.info("Set up availability for all tutors");
    }

    private void createSampleBookings() {
        logger.info("Creating sample bookings...");

        List<Student> students = studentRepository.findAll();
        List<Tutor> tutors = tutorRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll();

        // Create some past completed bookings
        createBooking(students.get(0), tutors.get(0), getSubjectByName(subjects, "Mathematics"),
                LocalDateTime.now().minusDays(10), 2, Booking.BookingStatus.COMPLETED);
        createBooking(students.get(1), tutors.get(1), getSubjectByName(subjects, "Computer Science"),
                LocalDateTime.now().minusDays(8), 1, Booking.BookingStatus.COMPLETED);
        createBooking(students.get(2), tutors.get(2), getSubjectByName(subjects, "Chemistry"),
                LocalDateTime.now().minusDays(5), 1, Booking.BookingStatus.COMPLETED);

        // Create some upcoming confirmed bookings
        createBooking(students.get(0), tutors.get(4), getSubjectByName(subjects, "Physics"),
                LocalDateTime.now().plusDays(2), 1, Booking.BookingStatus.CONFIRMED);
        createBooking(students.get(3), tutors.get(0), getSubjectByName(subjects, "Calculus"),
                LocalDateTime.now().plusDays(3), 2, Booking.BookingStatus.CONFIRMED);
        createBooking(students.get(4), tutors.get(5), getSubjectByName(subjects, "Spanish"),
                LocalDateTime.now().plusDays(5), 1, Booking.BookingStatus.CONFIRMED);

        // Create some pending bookings
        createBooking(students.get(5), tutors.get(3), getSubjectByName(subjects, "Piano"),
                LocalDateTime.now().plusDays(7), 1, Booking.BookingStatus.PENDING);
        createBooking(students.get(6), tutors.get(7), getSubjectByName(subjects, "English"),
                LocalDateTime.now().plusDays(8), 1, Booking.BookingStatus.PENDING);

        logger.info("Created sample bookings");
    }

    private void createSampleReviews() {
        logger.info("Creating sample reviews...");

        List<Booking> completedBookings = bookingRepository.findByStatus(Booking.BookingStatus.COMPLETED);

        if (!completedBookings.isEmpty()) {
            // Create reviews for completed bookings
            createReview(completedBookings.get(0).getStudentId(), completedBookings.get(0).getTutorId(),
                    5, "Excellent tutor! Very clear explanations and patient with questions. Highly recommended!");

            if (completedBookings.size() > 1) {
                createReview(completedBookings.get(1).getStudentId(), completedBookings.get(1).getTutorId(),
                        4, "Great session! The tutor was knowledgeable and helped me understand complex concepts.");
            }

            if (completedBookings.size() > 2) {
                createReview(completedBookings.get(2).getStudentId(), completedBookings.get(2).getTutorId(),
                        5, "Amazing chemistry tutor! Made organic chemistry actually understandable. Will book again!");
            }
        }

        logger.info("Created sample reviews");
    }

    private void createSampleNotifications() {
        logger.info("Creating sample notifications...");

        List<Student> students = studentRepository.findAll();
        List<Tutor> tutors = tutorRepository.findAll();

        // Create some sample notifications
        if (!students.isEmpty()) {
            notificationService.createNotification(
                    students.get(0).getId(),
                    NotificationType.BOOKING_REMINDER,
                    "Upcoming Session",
                    "You have a physics session tomorrow at 2:00 PM with Dr. Lisa Wang.");

            notificationService.createNotification(
                    students.get(1).getId(),
                    NotificationType.LOW_BALANCE_WARNING,
                    "Low Balance",
                    "Your account balance is running low. Consider adding funds to continue booking sessions.");
        }

        if (!tutors.isEmpty()) {
            notificationService.createNotification(
                    tutors.get(0).getId(),
                    NotificationType.NEW_STUDENT_REQUEST,
                    "New Booking Request",
                    "You have a new booking request for calculus tutoring.");
        }

        logger.info("Created sample notifications");
    }

    private Student createStudent(String name, String email, double balance) {
        Student student = new Student(name, email, passwordEncoder.encode("password123"));
        student.setBalance(balance);
        student.setTimeZone(ZoneId.of("America/New_York"));
        return student;
    }

    private Tutor createTutor(String name, String email, double hourlyRate, String description) {
        Tutor tutor = new Tutor(name, email, passwordEncoder.encode("password123"), hourlyRate, description);
        tutor.setTimeZone(ZoneId.of("America/New_York"));
        return tutor;
    }

    private List<Subject> getSubjectsByNames(List<Subject> subjects, String... names) {
        List<Subject> result = new ArrayList<>();
        for (String name : names) {
            subjects.stream()
                    .filter(s -> s.getName().equals(name))
                    .findFirst()
                    .ifPresent(result::add);
        }
        return result;
    }

    private Subject getSubjectByName(List<Subject> subjects, String name) {
        return subjects.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void assignSubjectsToTutor(Tutor tutor, List<Subject> subjects) {
        for (Subject subject : subjects) {
            tutor.addSubject(subject);
        }
        tutorRepository.update(tutor);
    }

    private void createBooking(Student student, Tutor tutor, Subject subject,
            LocalDateTime dateTime, int durationHours, Booking.BookingStatus status) {
        if (subject == null)
            return;

        Booking booking = new Booking(student.getId(), tutor.getId(), subject, dateTime, durationHours,
                tutor.getHourlyRate());
        booking.setStatus(status);

        // Create payment for confirmed and completed bookings
        if (status == Booking.BookingStatus.CONFIRMED || status == Booking.BookingStatus.COMPLETED) {
            Payment payment = new Payment(booking.getId(), booking.getTotalCost());
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            booking.setPayment(payment);

            // Deduct from student balance
            student.setBalance(student.getBalance() - booking.getTotalCost());
            studentRepository.update(student);

            // Add to tutor earnings if completed
            if (status == Booking.BookingStatus.COMPLETED) {
                tutor.setEarnings(tutor.getEarnings() + booking.getTotalCost());
                tutorRepository.update(tutor);
            }
        }

        bookingRepository.save(booking);
    }

    private void createReview(String studentId, String tutorId, int rating, String comment) {
        Review review = new Review(studentId, tutorId, rating, comment);
        reviewRepository.save(review);
    }

    private void printSummary() {
        logger.info("=== DATA INITIALIZATION SUMMARY ===");
        logger.info("Subjects: {}", subjectRepository.findAll().size());
        logger.info("Students: {}", studentRepository.findAll().size());
        logger.info("Tutors: {}", tutorRepository.findAll().size());
        logger.info("Bookings: {}", bookingRepository.findAll().size());
        logger.info("Reviews: {}", reviewRepository.findAll().size());
        logger.info("======================================");
        logger.info("Sample credentials:");
        logger.info("Student: alice.johnson@email.com / password123");
        logger.info("Tutor: sarah.mitchell@email.com / password123");
        logger.info("======================================");
    }
}