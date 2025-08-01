package com.tutoringplatform.search;

import com.tutoringplatform.shared.dto.request.TutorSearchRequest;
import com.tutoringplatform.shared.dto.response.TutorSearchResultsResponse;
import com.tutoringplatform.subject.exceptions.SubjectNotFoundException;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;

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
    public ResponseEntity<?> searchTutors(@RequestBody TutorSearchRequest request) throws SubjectNotFoundException, NoCompletedBookingsException, UserNotFoundException, PaymentNotFoundException {
        TutorSearchResultsResponse results = searchService.searchTutors(request);
        return ResponseEntity.ok(results);
    }
}