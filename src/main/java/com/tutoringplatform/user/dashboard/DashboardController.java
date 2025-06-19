package com.tutoringplatform.user.dashboard;

import com.tutoringplatform.shared.dto.response.StudentDashboardResponse;
import com.tutoringplatform.shared.dto.response.TutorDashboardResponse;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentDashboard(@PathVariable String studentId) throws UserNotFoundException, NoCompletedBookingsException, PaymentNotFoundException {
        StudentDashboardResponse dashboard = dashboardService.getStudentDashboard(studentId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorDashboard(@PathVariable String tutorId) throws UserNotFoundException, NoCompletedBookingsException {
        TutorDashboardResponse dashboard = dashboardService.getTutorDashboard(tutorId);
        return ResponseEntity.ok(dashboard);
    }
}
