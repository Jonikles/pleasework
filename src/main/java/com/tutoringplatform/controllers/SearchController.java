package com.tutoringplatform.controllers;

import com.tutoringplatform.dto.request.TutorSearchRequest;
import com.tutoringplatform.dto.response.TutorSearchResultsResponse;
import com.tutoringplatform.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/tutors")
    public ResponseEntity<?> searchTutors(@RequestBody TutorSearchRequest request) {
        try {
            TutorSearchResultsResponse results = searchService.searchTutors(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}