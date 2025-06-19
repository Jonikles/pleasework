package com.tutoringplatform.booking;

import com.tutoringplatform.booking.exceptions.BookingNotFoundException;
import com.tutoringplatform.booking.observer.BookingEvent;
import com.tutoringplatform.payment.Payment;
import com.tutoringplatform.payment.PaymentService;
import com.tutoringplatform.shared.dto.request.CreateBookingRequest;
import com.tutoringplatform.shared.dto.request.UpdateBookingRequest;
import com.tutoringplatform.shared.dto.response.BookingDetailResponse;
import com.tutoringplatform.shared.dto.response.BookingListResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;
import com.tutoringplatform.user.student.exceptions.InsufficientBalanceException;   
import com.tutoringplatform.subject.exceptions.SubjectNotFoundException;
import com.tutoringplatform.user.tutor.exceptions.TutorNotTeachingSubjectException;
import com.tutoringplatform.booking.exceptions.*;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.user.student.IStudentRepository;
import com.tutoringplatform.user.tutor.ITutorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final IBookingRepository bookingRepository;
    private final AvailabilityService availabilityService;
    private final PaymentService paymentService;
    private final DTOMapper dtoMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final IStudentRepository studentRepository;
    private final ITutorRepository tutorRepository;
    private final SubjectService subjectService;

    @Autowired
    public BookingService(
            IBookingRepository bookingRepository,
            SubjectService subjectService,
            AvailabilityService availabilityService,
            PaymentService paymentService,
            DTOMapper dtoMapper,
            ApplicationEventPublisher eventPublisher,
            IStudentRepository studentRepository,
            ITutorRepository tutorRepository) {
        this.bookingRepository = bookingRepository;
        this.subjectService = subjectService;
        this.availabilityService = availabilityService;
        this.paymentService = paymentService;
        this.dtoMapper = dtoMapper;
        this.eventPublisher = eventPublisher;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
    }

    @Transactional
    public BookingDetailResponse createBooking(CreateBookingRequest request)
        throws UserNotFoundException, InsufficientBalanceException,
        SubjectNotFoundException, TutorNotTeachingSubjectException, TutorNotAvailableException,
        BookedTimeSlotException {

        logger.debug("Creating booking for student {} and tutor {}", request.getStudentId(), request.getTutorId());
        // Fetch entities
        Student student = studentRepository.findById(request.getStudentId());
        Tutor tutor = tutorRepository.findById(request.getTutorId());

        if (student.getBalance() < request.getDurationHours() * tutor.getHourlyRate()) {
            logger.warn("Student {} does not have enough money to book this tutor", student.getId());
            throw new InsufficientBalanceException(student.getId(), request.getDurationHours() * tutor.getHourlyRate(), student.getBalance());
        }

        Subject subject = subjectService.findById(request.getSubjectId());

        // Validate tutor teaches this subject
        if (!tutor.getSubjects().contains(subject)) {
            logger.warn("Tutor {} does not teach this subject", tutor.getId());
            throw new TutorNotTeachingSubjectException(tutor.getId(), subject.getId());
        }

        // Check availability
        ZonedDateTime startTime = request.getDateTime().atZone(student.getTimeZone());
        ZonedDateTime endTime = startTime.plusHours(request.getDurationHours());

        if (!availabilityService.isAvailable(tutor.getId(), startTime, endTime, student.getTimeZone())) {
            logger.warn("Tutor {} is not available from {} to {}", tutor.getId(), startTime, endTime);
            throw new TutorNotAvailableException(tutor.getId(), startTime, endTime);
        }

        // Check for conflicts
        List<Booking> existingBookings = bookingRepository.findByTutorIdAndDateTimeRange(
                tutor.getId(),
                request.getDateTime(),
                request.getDateTime().plusHours(request.getDurationHours()));

        if (!existingBookings.isEmpty()) {
            logger.warn("Time slot already booked for tutor {} from {} to {}", tutor.getId(), startTime, endTime);
            throw new BookedTimeSlotException(tutor.getId(), startTime, endTime);
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
        logger.info("Booking {} created successfully for student {} and tutor {}", booking.getId(), student.getId(), tutor.getId());

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

    public BookingDetailResponse getBookingDetails(String bookingId) throws BookingNotFoundException, UserNotFoundException, PaymentNotFoundException {
        logger.debug("Getting booking details: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            logger.warn("Booking not found: {}", bookingId);
            throw new BookingNotFoundException(bookingId);
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());
        Payment payment = paymentService.findById(bookingId);

        logger.info("Booking details found successfully: {}", bookingId);
        return dtoMapper.toBookingDetailResponse(booking, student, tutor, payment);
    }

    public List<Booking> getStudentBookingList(String studentId) throws UserNotFoundException, PaymentNotFoundException {
        logger.debug("Getting booking list for student: {}", studentId);
        studentRepository.findById(studentId);

        List<Booking> allBookings = bookingRepository.findByStudentId(studentId);
        logger.info("Booking list of size {} found successfully for student: {}", allBookings.size(), studentId);
        return allBookings;
    }

    public BookingListResponse getStudentBookingListResponse(String studentId)
            throws UserNotFoundException, PaymentNotFoundException {
        logger.debug("Getting booking list for student: {}", studentId);
        studentRepository.findById(studentId);
        List<Booking> allBookings = getStudentBookingList(studentId);
        return categorizeAndEnrichBookings(allBookings);
    }

    public List<Booking> getTutorBookingList(String tutorId) throws UserNotFoundException {
        logger.debug("Getting booking list for tutor: {}", tutorId);
        tutorRepository.findById(tutorId);

        List<Booking> allBookings = bookingRepository.findByTutorId(tutorId);
        logger.info("Booking list of size {} found successfully for tutor: {}", allBookings.size(), tutorId);
        return allBookings;
    }

    public BookingListResponse getTutorBookingListResponse(String tutorId) throws UserNotFoundException, PaymentNotFoundException {
        logger.debug("Getting booking list for tutor: {}", tutorId);
        tutorRepository.findById(tutorId);
        List<Booking> allBookings = getTutorBookingList(tutorId);
        return categorizeAndEnrichBookings(allBookings);
    }

    @Transactional
    public BookingDetailResponse updateBooking(String bookingId, UpdateBookingRequest request)
        throws BookingNotFoundException, TutorNotAvailableException, UserNotFoundException {
        logger.debug("Updating booking: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            logger.error("Booking not found: {}", bookingId);
            throw new BookingNotFoundException(bookingId);
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            logger.warn("Booking {} is in {} status", bookingId, booking.getStatus());
            throw new IllegalStateException("Can only update pending bookings");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        if (request.getDateTime() != null) {
            ZonedDateTime startTime = request.getDateTime().atZone(student.getTimeZone());
            ZonedDateTime endTime = startTime.plusHours(
                    request.getDurationHours() != 0 ? request.getDurationHours() : booking.getDurationHours());

            if (!availabilityService.isAvailable(tutor.getId(), startTime, endTime, student.getTimeZone())) {
                logger.warn("Tutor {} is not available at the new time", tutor.getId());
                throw new TutorNotAvailableException(tutor.getId(), startTime, endTime);
            }

            booking.setDateTime(request.getDateTime());
        }

        if (request.getDurationHours() != 0) {
            booking.setDurationHours(request.getDurationHours());
            booking.setTotalCost(tutor.getHourlyRate() * request.getDurationHours());
        }

        bookingRepository.update(booking);

        logger.info("Booking {} updated successfully", bookingId);
        return dtoMapper.toBookingDetailResponse(booking, student, tutor, null);
    }

    @Transactional
    public BookingDetailResponse confirmBooking(String bookingId)
        throws BookingNotFoundException, InsufficientBalanceException, UserNotFoundException {
        logger.debug("Confirming booking {}", bookingId);

            Booking booking = findBooking(bookingId);

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            logger.warn("Booking {} is in {} status", bookingId, booking.getStatus());
            throw new IllegalStateException("Booking is not in pending status");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        Payment payment = paymentService.processPayment(
                student.getId(),
                bookingId,
                booking.getTotalCost());

        booking.setPayment(payment);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.update(booking);

        logger.info("Booking {} confirmed. Payment: {}", bookingId, payment.getId());

        eventPublisher.publishEvent(new BookingEvent(
                this,
                BookingEvent.EventType.CONFIRMED,
                booking,
                student,
                tutor));

        return dtoMapper.toBookingDetailResponse(booking, student, tutor, payment);
    }

    @Transactional
    public void cancelBooking(String bookingId)
        throws BookingNotFoundException, PaymentNotFoundException, UserNotFoundException {
        logger.debug("Cancelling booking {}", bookingId);

        Booking booking = findBooking(bookingId);

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            logger.warn("Cannot cancel completed booking: {}", bookingId);
            throw new IllegalStateException("Cannot cancel completed booking");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED && booking.getPayment() != null) {
            paymentService.refundPayment(booking.getPayment().getId());
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.update(booking);

        logger.info("Booking {} cancelled successfully", bookingId);

        eventPublisher.publishEvent(new BookingEvent(
                this,
                BookingEvent.EventType.CANCELLED,
                booking,
                student,
                tutor));
    }

    @Transactional
    public void completeBooking(String bookingId)
        throws BookingNotFoundException, UserNotFoundException {
        logger.debug("Completing booking {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            logger.error("Booking not found: {}", bookingId);
            throw new BookingNotFoundException(bookingId);
        }

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            logger.warn("Booking {} is not in confirmed status", bookingId);
            throw new IllegalStateException("Booking must be confirmed first");
        }

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        bookingRepository.update(booking);

        tutor.setEarnings(tutor.getEarnings() + booking.getTotalCost());
        tutorRepository.update(tutor);

        eventPublisher.publishEvent(new BookingEvent(
                this,
                BookingEvent.EventType.COMPLETED,
                booking,
                student,
                tutor));

        logger.info("Booking {} completed successfully", bookingId);
    }

    // Helper method to categorize and enrich bookings
    private BookingListResponse categorizeAndEnrichBookings(List<Booking> bookings) throws UserNotFoundException, PaymentNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        List<BookingDetailResponse> upcomingBookings = new ArrayList<>();
        List<BookingDetailResponse> pastBookings = new ArrayList<>();
        List<BookingDetailResponse> cancelledBookings = new ArrayList<>();

        for (Booking booking : bookings) {
            Student student = studentRepository.findById(booking.getStudentId());
            Tutor tutor = tutorRepository.findById(booking.getTutorId());
            Payment payment = paymentService.findById(booking.getId());

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

    private Booking findBooking(String bookingId) throws BookingNotFoundException {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            logger.warn("Booking not found: {}", bookingId);
            throw new BookingNotFoundException(bookingId);
        }
        return booking;
    }

    public List<Booking> getTutorBookingsBySubject(String tutorId, String subjectId) {
        logger.debug("Getting bookings for tutor {} and subject {}", tutorId, subjectId);
        return bookingRepository.findByTutorIdAndSubjectId(tutorId, subjectId);
    }

    public List<Booking> hasStudentCompletedBookingWithTutor(String studentId, String tutorId, Booking.BookingStatus status) throws UserNotFoundException {
        logger.debug("Checking if student {} has completed booking with tutor {}", studentId, tutorId);
        studentRepository.findById(studentId);
        tutorRepository.findById(tutorId);

        List<Booking> bookings = bookingRepository.findByStudentIdAndTutorIdAndStatus(studentId, tutorId, status);
        return bookings;
    }
}