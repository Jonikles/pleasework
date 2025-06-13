// FILE: src/main/java/com/tutoringplatform/controllers/DashboardController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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

    // Future: Add tutor dashboard endpoint
}