// FILE: src/main/java/com/tutoringplatform/controllers/SearchController.java
package com.tutoringplatform.controllers;

import com.tutoringplatform.services.SearchService;
import com.tutoringplatform.dto.response.TutorSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/tutors")
    public ResponseEntity<?> searchTutors(
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) LocalDateTime availableFrom,
            @RequestParam(required = false) LocalDateTime availableTo,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailableNow) {

        try {
            SearchService.TutorSearchCriteria criteria = new SearchService.TutorSearchCriteria.Builder()
                    .withSubject(subjectId)
                    .withPriceRange(minPrice, maxPrice)
                    .withMinRating(minRating)
                    .withSearchText(searchText)
                    .withAvailability(availableFrom, availableTo)
                    .onlyAvailableNow(onlyAvailableNow)
                    .build();

            List<TutorSearchResponse> results = searchService.searchTutors(criteria);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}