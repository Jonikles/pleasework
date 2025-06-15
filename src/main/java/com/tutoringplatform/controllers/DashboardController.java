package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.response.StudentDashboardResponse;
import com.tutoringplatform.dto.response.TutorDashboardResponse;
import com.tutoringplatform.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentDashboard(@PathVariable String studentId) {
        try {
            StudentDashboardResponse dashboard = dashboardService.getStudentDashboard(studentId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorDashboard(@PathVariable String tutorId) {
        try {
            TutorDashboardResponse dashboard = dashboardService.getTutorDashboard(tutorId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
