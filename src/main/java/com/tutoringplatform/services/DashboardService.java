// FILE: src/main/java/com/tutoringplatform/services/DashboardService.java
package com.tutoringplatform.services;

import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.models.*;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final StudentService studentService;
    private final BookingService bookingService;
    private final TutorService tutorService;
    private final SubjectService subjectService;
    private final ReviewService reviewService;
    private final SearchService searchService;
    private final DTOMapper dtoMapper;

    @Autowired
    public DashboardService(StudentService studentService, BookingService bookingService, TutorService tutorService,
            SubjectService subjectService, ReviewService reviewService, SearchService searchService,
            DTOMapper dtoMapper) {
        this.studentService = studentService;
        this.bookingService = bookingService;
        this.tutorService = tutorService;
        this.subjectService = subjectService;
        this.reviewService = reviewService;
        this.searchService = searchService;
        this.dtoMapper = dtoMapper;
    }

    public StudentDashboardResponse getStudentDashboard(String studentId) throws Exception {
        StudentDashboardResponse dashboard = new StudentDashboardResponse();

        // Get student info
        Student student = studentService.findById(studentId);
        dashboard.setStudent(dtoMapper.toStudentResponse(student));

        // Get bookings with enriched data
        List<Booking> allBookings = bookingService.findByStudentId(studentId);

        LocalDateTime now = LocalDateTime.now();
        List<EnrichedBookingResponse> upcomingBookings = new ArrayList<>();
        List<EnrichedBookingResponse> pastBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            EnrichedBookingResponse enriched = enrichBooking(booking);

            if (booking.getDateTime().isAfter(now) &&
                    booking.getStatus() != Booking.BookingStatus.CANCELLED) {
                upcomingBookings.add(enriched);
            } else {
                pastBookings.add(enriched);
            }
        }

        dashboard.setUpcomingBookings(upcomingBookings);
        dashboard.setPastBookings(pastBookings);

        // Get all subjects
        List<Subject> subjects = subjectService.findAll();
        dashboard.setAvailableSubjects(subjects.stream()
                .map(dtoMapper::toSubjectResponse)
                .collect(Collectors.toList()));

        return dashboard;
    }

    private EnrichedBookingResponse enrichBooking(Booking booking) throws Exception {
        EnrichedBookingResponse enriched = new EnrichedBookingResponse();

        // Copy basic booking data
        enriched.setId(booking.getId());
        enriched.setStudentId(booking.getStudentId());
        enriched.setTutorId(booking.getTutorId());
        enriched.setSubject(dtoMapper.toSubjectResponse(booking.getSubject()));
        enriched.setDateTime(booking.getDateTime());
        enriched.setDurationHours(booking.getDurationHours());
        enriched.setTotalCost(booking.getTotalCost());
        enriched.setStatus(booking.getStatus().toString());

        // Enrich with names
        try {
            Student student = studentService.findById(booking.getStudentId());
            enriched.setStudentName(student.getName());
        } catch (Exception e) {
            enriched.setStudentName("Unknown");
        }

        try {
            Tutor tutor = tutorService.findById(booking.getTutorId());
            enriched.setTutorName(tutor.getName());
        } catch (Exception e) {
            enriched.setTutorName("Unknown");
        }

        return enriched;
    }

    public TutorDashboardResponse getTutorDashboard(String tutorId) throws Exception {
        TutorDashboardResponse dashboard = new TutorDashboardResponse();

        // Get tutor info
        Tutor tutor = tutorService.findById(tutorId);
        dashboard.setTutor(dtoMapper.toTutorResponse(tutor));

        // Get bookings
        List<Booking> allBookings = bookingService.findByTutorId(tutorId);
        LocalDateTime now = LocalDateTime.now();

        List<EnrichedBookingResponse> upcomingBookings = new ArrayList<>();
        List<EnrichedBookingResponse> recentBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            EnrichedBookingResponse enriched = enrichBooking(booking);

            if (booking.getDateTime().isAfter(now) &&
                    booking.getStatus() != Booking.BookingStatus.CANCELLED) {
                upcomingBookings.add(enriched);
            } else if (booking.getDateTime().isAfter(now.minusDays(30))) {
                recentBookings.add(enriched);
            }
        }

        dashboard.setUpcomingBookings(upcomingBookings);
        dashboard.setRecentBookings(recentBookings);

        // Get recent reviews
        List<Review> reviews = reviewService.getTutorReviews(tutorId);
        List<ReviewResponse> recentReviews = reviews.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(5)
                .map(dtoMapper::toReviewResponse)
                .collect(Collectors.toList());
        dashboard.setRecentReviews(recentReviews);

        // Calculate stats
        DashboardStats stats = new DashboardStats();
        stats.setTotalBookings(allBookings.size());
        stats.setCompletedSessions((int) allBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count());
        stats.setTotalEarnings(tutor.getEarnings());
        stats.setAverageRating(tutor.getReviewsReceived().isEmpty() ? 0.0
                : tutor.getReviewsReceived().stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0));
        stats.setTotalReviews(tutor.getReviewsReceived().size());

        dashboard.setStats(stats);

        return dashboard;
    }

    public List<TutorSearchResponse> searchTutorsEnriched(String subjectId, Double minPrice, 
        Double maxPrice, Double minRating) throws Exception {
    
        SearchService.TutorSearchCriteria criteria = new SearchService.TutorSearchCriteria.Builder()
            .withSubject(subjectId)
            .withPriceRange(minPrice, maxPrice)
            .withMinRating(minRating)
            .build();
        
        return searchService.searchTutors(criteria);
    }   
}