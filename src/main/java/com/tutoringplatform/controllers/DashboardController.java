// FILE: src/main/java/com/tutoringplatform/controllers/DashboardController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

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

    @GetMapping("/search/tutors")
    public ResponseEntity<?> searchTutors(
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating) {
        try {
            List<TutorSearchResponse> results = dashboardService.searchTutorsEnriched(
                    subjectId, minPrice, maxPrice, minRating);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}