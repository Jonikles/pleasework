package com.tutoringplatform.booking.observer;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

@Component
public class EmailNotifier {

    private final EmailService emailService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");

    @Autowired
    public EmailNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onBookingEvent(BookingEvent event) {
        switch (event.getEventType()) {
            case CREATED:
                sendBookingCreatedEmails(event.getBooking(), event.getStudent(), event.getTutor());
                break;
            case CONFIRMED:
                sendBookingConfirmedEmails(event.getBooking(), event.getStudent(), event.getTutor());
                break;
            case CANCELLED:
                sendBookingCancelledEmails(event.getBooking(), event.getStudent(), event.getTutor());
                break;
            case COMPLETED:
                sendBookingCompletedEmails(event.getBooking(), event.getStudent(), event.getTutor());
                break;
        }
    }

    private void sendBookingCreatedEmails(Booking booking, Student student, Tutor tutor) {
        // Email to student
        String studentSubject = "Booking Created - Confirmation Required";
        String studentBody = String.format(
                "Dear %s,\n\n" +
                        "Your booking has been created successfully!\n\n" +
                        "Details:\n" +
                        "- Tutor: %s\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n" +
                        "- Duration: %d hour(s)\n" +
                        "- Total Cost: $%.2f\n\n" +
                        "Please confirm and pay for this booking to secure your session.\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                student.getName(),
                tutor.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter),
                booking.getDurationHours(),
                booking.getTotalCost());

        emailService.sendEmail(student.getEmail(), studentSubject, studentBody);

        // Email to tutor
        String tutorSubject = "New Booking Request";
        String tutorBody = String.format(
                "Dear %s,\n\n" +
                        "You have received a new booking request!\n\n" +
                        "Details:\n" +
                        "- Student: %s\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n" +
                        "- Duration: %d hour(s)\n\n" +
                        "This booking is pending confirmation and payment from the student.\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                tutor.getName(),
                student.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter),
                booking.getDurationHours());

        emailService.sendEmail(tutor.getEmail(), tutorSubject, tutorBody);
    }

    private void sendBookingConfirmedEmails(Booking booking, Student student, Tutor tutor) {
        // Email to student
        String studentSubject = "Booking Confirmed - Payment Successful";
        String studentBody = String.format(
                "Dear %s,\n\n" +
                        "Your booking has been confirmed and payment processed successfully!\n\n" +
                        "Confirmed Session Details:\n" +
                        "- Tutor: %s\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n" +
                        "- Duration: %d hour(s)\n" +
                        "- Amount Paid: $%.2f\n\n" +
                        "We'll send you a reminder before your session.\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                student.getName(),
                tutor.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter),
                booking.getDurationHours(),
                booking.getTotalCost());

        emailService.sendEmail(student.getEmail(), studentSubject, studentBody);

        // Email to tutor
        String tutorSubject = "Booking Confirmed - Session Scheduled";
        String tutorBody = String.format(
                "Dear %s,\n\n" +
                        "Great news! Your booking has been confirmed.\n\n" +
                        "Confirmed Session Details:\n" +
                        "- Student: %s\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n" +
                        "- Duration: %d hour(s)\n" +
                        "- Your Earnings: $%.2f\n\n" +
                        "Please ensure you're available at the scheduled time.\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                tutor.getName(),
                student.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter),
                booking.getDurationHours(),
                booking.getTotalCost());

        emailService.sendEmail(tutor.getEmail(), tutorSubject, tutorBody);
    }

    private void sendBookingCancelledEmails(Booking booking, Student student, Tutor tutor) {
        // Email to student
        String studentSubject = "Booking Cancelled";
        String studentBody = String.format(
                "Dear %s,\n\n" +
                        "Your booking has been cancelled.\n\n" +
                        "Cancelled Session Details:\n" +
                        "- Tutor: %s\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n\n" +
                        "%s\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                student.getName(),
                tutor.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter),
                booking.getPayment() != null ? "Your payment has been refunded." : "");

        emailService.sendEmail(student.getEmail(), studentSubject, studentBody);

        // Email to tutor
        String tutorSubject = "Booking Cancelled";
        String tutorBody = String.format(
                "Dear %s,\n\n" +
                        "A booking has been cancelled.\n\n" +
                        "Cancelled Session Details:\n" +
                        "- Student: %s\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n\n" +
                        "This time slot is now available for other bookings.\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                tutor.getName(),
                student.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter));

        emailService.sendEmail(tutor.getEmail(), tutorSubject, tutorBody);
    }

    private void sendBookingCompletedEmails(Booking booking, Student student, Tutor tutor) {
        // Email to student
        String studentSubject = "Session Completed - Leave a Review";
        String studentBody = String.format(
                "Dear %s,\n\n" +
                        "We hope you had a great session with %s!\n\n" +
                        "Completed Session Details:\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n" +
                        "- Duration: %d hour(s)\n\n" +
                        "Please take a moment to leave a review for your tutor. " +
                        "Your feedback helps other students make informed decisions.\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                student.getName(),
                tutor.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter),
                booking.getDurationHours());

        emailService.sendEmail(student.getEmail(), studentSubject, studentBody);

        // Email to tutor
        String tutorSubject = "Session Completed - Payment Released";
        String tutorBody = String.format(
                "Dear %s,\n\n" +
                        "Your session has been marked as completed!\n\n" +
                        "Completed Session Details:\n" +
                        "- Student: %s\n" +
                        "- Subject: %s\n" +
                        "- Date & Time: %s\n" +
                        "- Duration: %d hour(s)\n" +
                        "- Earnings: $%.2f\n\n" +
                        "The payment has been added to your earnings.\n\n" +
                        "Best regards,\n" +
                        "Tutoring Platform Team",
                tutor.getName(),
                student.getName(),
                booking.getSubject().getName(),
                booking.getDateTime().format(formatter),
                booking.getDurationHours(),
                booking.getTotalCost());

        emailService.sendEmail(tutor.getEmail(), tutorSubject, tutorBody);
    }
}