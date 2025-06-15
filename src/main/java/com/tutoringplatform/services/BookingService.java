package com.tutoringplatform.services;

import java.time.LocalDateTime;
import java.util.List;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.tutoringplatform.models.Booking;
import com.tutoringplatform.models.Payment;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.observer.BookingEvent;
import com.tutoringplatform.repositories.interfaces.IBookingRepository;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import com.tutoringplatform.repositories.interfaces.ISubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class BookingService {
    private final IBookingRepository bookingRepository;
    private final IStudentRepository studentRepository;
    private final ITutorRepository tutorRepository;
    private final PaymentService paymentService;
    private final ISubjectRepository subjectRepository;
    private final AvailabilityService availabilityService;
    private final TutorService tutorService;
    private ApplicationEventPublisher eventPublisher;

    @Autowired 
    public BookingService(IBookingRepository bookingRepository, IStudentRepository studentRepository,
            ITutorRepository tutorRepository, PaymentService paymentService, ISubjectRepository subjectRepository,
            AvailabilityService availabilityService, TutorService tutorService, ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.paymentService = paymentService;
        this.subjectRepository = subjectRepository;
        this.availabilityService = availabilityService;
        this.tutorService = tutorService;
        this.eventPublisher = eventPublisher;
    }

    public Booking createBooking(String studentId, String tutorId, String subjectId,
            LocalDateTime dateTime, int durationHours) throws Exception {
        Subject subject = subjectRepository.findById(subjectId);
        Student student = studentRepository.findById(studentId);
        Tutor tutor = tutorRepository.findById(tutorId);

        if (!tutor.getSubjects().contains(subject)) {
            throw new Exception("Tutor does not teach this subject");
        }

        // NEW: Use availability service with timezone support
        // Assume student is in same timezone as server for now (should get from student
        // profile)
        ZoneId studentTimeZone = student.getTimeZone();
        ZonedDateTime startTime = dateTime.atZone(studentTimeZone);
        ZonedDateTime endTime = startTime.plusHours(durationHours);

        if (!availabilityService.isAvailable(tutorId, startTime, endTime, studentTimeZone)) {
            throw new Exception("Tutor is not available at this time");
        }

        // Check for booking conflicts
        List<Booking> tutorBookings = bookingRepository.findByTutorId(tutorId);
        for (Booking b : tutorBookings) {
            if (b.getStatus() != Booking.BookingStatus.CANCELLED &&
                    isTimeConflict(b, dateTime, durationHours)) {
                throw new Exception("Time slot already booked");
            }
        }

        Booking booking = new Booking(studentId, tutorId, subject, dateTime, durationHours, tutor.getHourlyRate());
        bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingEvent(this, BookingEvent.EventType.CREATED, booking, student, tutor));

        return booking;
    }

    private boolean isTimeConflict(Booking existing, LocalDateTime newTime, int newDuration) {
        LocalDateTime existingStart = existing.getDateTime();
        LocalDateTime existingEnd = existingStart.plusHours(existing.getDurationHours());

        LocalDateTime newStart = newTime;
        LocalDateTime newEnd = newStart.plusHours(newDuration);

        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    @Transactional
    public Booking confirmBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new Exception("Booking is not in pending status");
        }
        Student student = studentRepository.findById(booking.getStudentId());

        if (student != null) {
            student.addBooking(booking);
            studentRepository.update(student);
        } else {
            System.err.println("Student with ID " + booking.getStudentId() + " not found during booking confirmation.");
            return null;
        }

        Payment payment = paymentService.processPayment(student.getId(), bookingId, booking.getTotalCost());

        booking.setPayment(payment);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        Tutor tutor = tutorRepository.findById(booking.getTutorId());
        
        if (tutor != null) {
            tutor.addBooking(booking);
            tutorRepository.update(tutor);
        } else {
            System.err.println("Tutor with ID " + booking.getTutorId() + " not found during booking confirmation.");
        }

        bookingRepository.update(booking);

        eventPublisher.publishEvent(new BookingEvent(this, BookingEvent.EventType.CONFIRMED, booking, student, tutor));

        return booking;
    }

    public void cancelBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new Exception("Cannot cancel completed booking");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.update(booking);

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        if (student != null) {
            student.removeBooking(booking);
            studentRepository.update(student);
        } else {
            System.err.println("Student with ID " + booking.getStudentId() + " not found during booking cancellation.");
        }

        if (tutor != null) {
            tutor.removeBooking(booking);
            tutorRepository.update(tutor);
        } else {
            System.err.println("Tutor with ID " + booking.getTutorId() + " not found during booking cancellation.");
        }

        eventPublisher.publishEvent(new BookingEvent(this, BookingEvent.EventType.CANCELLED, booking, student, tutor));
    }

    @Transactional
    public void completeBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new Exception("Booking must be confirmed first");
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        bookingRepository.update(booking);

        tutorService.addEarnings(booking.getTutorId(), booking.getTotalCost());

        Tutor tutor = tutorRepository.findById(booking.getTutorId());
        Student student = studentRepository.findById(booking.getStudentId());
        
        eventPublisher.publishEvent(new BookingEvent(this, BookingEvent.EventType.COMPLETED, booking, student, tutor));
    }

    public Booking findById(String id) throws Exception {
        Booking booking = bookingRepository.findById(id);
        if (booking == null) {
            throw new Exception("Booking not found");
        }
        return booking;
    }

    public List<Booking> findByStudentId(String studentId) {
        return bookingRepository.findByStudentId(studentId);
    }

    public List<Booking> findByTutorId(String tutorId) {
        return bookingRepository.findByTutorId(tutorId);
    }
}