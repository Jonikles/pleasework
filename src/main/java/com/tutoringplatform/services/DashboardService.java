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

    @Autowired
    private StudentService studentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TutorService tutorService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private DTOMapper dtoMapper;

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
}