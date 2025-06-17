package com.tutoringplatform.booking;

import com.tutoringplatform.booking.observer.BookingEvent;
import com.tutoringplatform.payment.IPaymentRepository;
import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.payment.PaymentService;
import com.tutoringplatform.shared.dto.request.CreateBookingRequest;
import com.tutoringplatform.shared.dto.request.UpdateBookingRequest;
import com.tutoringplatform.shared.dto.response.BookingDetailResponse;
import com.tutoringplatform.shared.dto.response.BookingListResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.ISubjectRepository;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.ITutorRepository;
import com.tutoringplatform.user.tutor.Tutor;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class BookingService {

    private final IBookingRepository bookingRepository;
    private final IStudentRepository studentRepository;
    private final ITutorRepository tutorRepository;
    private final ISubjectRepository subjectRepository;
    private final IPaymentRepository paymentRepository;
    private final AvailabilityService availabilityService;
    private final PaymentService paymentService;
    private final DTOMapper dtoMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public BookingService(
            IBookingRepository bookingRepository,
            IStudentRepository studentRepository,
            ITutorRepository tutorRepository,
            ISubjectRepository subjectRepository,
            IPaymentRepository paymentRepository,
            AvailabilityService availabilityService,
            PaymentService paymentService,
            DTOMapper dtoMapper,
            ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.subjectRepository = subjectRepository;
        this.paymentRepository = paymentRepository;
        this.availabilityService = availabilityService;
        this.paymentService = paymentService;
        this.dtoMapper = dtoMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BookingDetailResponse createBooking(CreateBookingRequest request) throws Exception {
        // Fetch entities
        Student student = studentRepository.findById(request.getStudentId());
        if (student == null) {
            throw new Exception("Student not found");
        }

        Tutor tutor = tutorRepository.findById(request.getTutorId());
        if (tutor == null) {
            throw new Exception("Tutor not found");
        }

        if (student.getBalance() < request.getDurationHours() * tutor.getHourlyRate()) {
            throw new Exception("Student does not have enough money to book this tutor");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId());
        if (subject == null) {
            throw new Exception("Subject not found");
        }

        // Validate tutor teaches this subject
        if (!tutor.getSubjects().contains(subject)) {
            throw new Exception("Tutor does not teach this subject");
        }

        // Check availability
        ZonedDateTime startTime = request.getDateTime().atZone(student.getTimeZone());
        ZonedDateTime endTime = startTime.plusHours(request.getDurationHours());

        if (!availabilityService.isAvailable(tutor.getId(), startTime, endTime, student.getTimeZone())) {
            throw new Exception("Tutor is not available at this time");
        }

        // Check for conflicts
        List<Booking> existingBookings = bookingRepository.findByTutorIdAndDateTimeRange(
                tutor.getId(),
                request.getDateTime(),
                request.getDateTime().plusHours(request.getDurationHours()));

        if (!existingBookings.isEmpty()) {
            throw new Exception("Time slot already booked");
        }

        // Create booking
        Booking booking = new Booking(
                student.getId(),
                tutor.getId(),
                subject,
                request.getDateTime(),
                request.getDurationHours(),
                tutor.getHourlyRate());

        bookingRepository.save(booking);

        // Publish event
        eventPublisher.publishEvent(new BookingEvent(
                this,
                BookingEvent.EventType.CREATED,
                booking,
                student,
                tutor));

        // Return detailed response - no payment yet as booking is PENDING
        return dtoMapper.toBookingDetailResponse(booking, student, tutor, null);
    }

    public BookingDetailResponse getBookingDetails(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());
        Payment payment = paymentRepository.findByBookingId(bookingId);

        return dtoMapper.toBookingDetailResponse(booking, student, tutor, payment);
    }

    public BookingListResponse getStudentBookingList(String studentId) throws Exception {
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new Exception("Student not found");
        }

        List<Booking> allBookings = bookingRepository.findByStudentId(studentId);
        return categorizeAndEnrichBookings(allBookings);
    }

    public BookingListResponse getTutorBookingList(String tutorId) throws Exception {
        Tutor tutor = tutorRepository.findById(tutorId);
        if (tutor == null) {
            throw new Exception("Tutor not found");
        }

        List<Booking> allBookings = bookingRepository.findByTutorId(tutorId);
        return categorizeAndEnrichBookings(allBookings);
    }

    @Transactional
    public BookingDetailResponse updateBooking(String bookingId, UpdateBookingRequest request) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new Exception("Can only update pending bookings");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        // Update date/time if provided
        if (request.getDateTime() != null) {
            // Check new time availability
            ZonedDateTime startTime = request.getDateTime().atZone(student.getTimeZone());
            ZonedDateTime endTime = startTime.plusHours(
                    request.getDurationHours() != 0 ? request.getDurationHours() : booking.getDurationHours());

            if (!availabilityService.isAvailable(tutor.getId(), startTime, endTime, student.getTimeZone())) {
                throw new Exception("Tutor is not available at the new time");
            }

            booking.setDateTime(request.getDateTime());
        }

        // Update duration if provided
        if (request.getDurationHours() != 0) {
            booking.setDurationHours(request.getDurationHours());
            booking.setTotalCost(tutor.getHourlyRate() * request.getDurationHours());
        }

        bookingRepository.update(booking);

        return dtoMapper.toBookingDetailResponse(booking, student, tutor, null);
    }

    @Transactional
    public BookingDetailResponse confirmBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new Exception("Booking is not in pending status");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        // Process payment
        Payment payment = paymentService.processPayment(
                student.getId(),
                bookingId,
                booking.getTotalCost());

        // Update booking
        booking.setPayment(payment);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.update(booking);

        // Update user bookings
        student.addBooking(booking);
        tutor.addBooking(booking);
        studentRepository.update(student);
        tutorRepository.update(tutor);

        // Publish event
        eventPublisher.publishEvent(new BookingEvent(
                this,
                BookingEvent.EventType.CONFIRMED,
                booking,
                student,
                tutor));

        return dtoMapper.toBookingDetailResponse(booking, student, tutor, payment);
    }

    @Transactional
    public void cancelBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new Exception("Cannot cancel completed booking");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        // If confirmed, process refund
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED && booking.getPayment() != null) {
            paymentService.refundPayment(booking.getPayment().getId());
        }

        // Update status
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.update(booking);

        // Remove from user bookings
        student.removeBooking(booking);
        tutor.removeBooking(booking);
        studentRepository.update(student);
        tutorRepository.update(tutor);

        // Publish event
        eventPublisher.publishEvent(new BookingEvent(
                this,
                BookingEvent.EventType.CANCELLED,
                booking,
                student,
                tutor));
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

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        // Update status
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        bookingRepository.update(booking);

        // Add earnings to tutor
        tutor.setEarnings(tutor.getEarnings() + booking.getTotalCost());
        tutorRepository.update(tutor);

        // Publish event
        eventPublisher.publishEvent(new BookingEvent(
                this,
                BookingEvent.EventType.COMPLETED,
                booking,
                student,
                tutor));
    }

    // Helper method to categorize and enrich bookings
    private BookingListResponse categorizeAndEnrichBookings(List<Booking> bookings) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<BookingDetailResponse> upcomingBookings = new ArrayList<>();
        List<BookingDetailResponse> pastBookings = new ArrayList<>();
        List<BookingDetailResponse> cancelledBookings = new ArrayList<>();

        for (Booking booking : bookings) {
            Student student = studentRepository.findById(booking.getStudentId());
            Tutor tutor = tutorRepository.findById(booking.getTutorId());
            Payment payment = paymentRepository.findByBookingId(booking.getId());

            BookingDetailResponse detail = dtoMapper.toBookingDetailResponse(
                    booking, student, tutor, payment);

            if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                cancelledBookings.add(detail);
            } else if (booking.getDateTime().isAfter(now)) {
                upcomingBookings.add(detail);
            } else {
                pastBookings.add(detail);
            }
        }

        return dtoMapper.toBookingListResponse(upcomingBookings, pastBookings, cancelledBookings);
    }
}