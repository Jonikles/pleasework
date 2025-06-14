// FILE: src/main/java/com/tutoringplatform/services/SearchService.java
package com.tutoringplatform.services;

import com.tutoringplatform.models.*;
import com.tutoringplatform.dto.response.*;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private TutorService tutorService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private DTOMapper dtoMapper;

    public static class TutorSearchCriteria {
        private String subjectId;
        private Double minPrice;
        private Double maxPrice;
        private Double minRating;
        private LocalDateTime availableFrom;
        private LocalDateTime availableTo;
        private String searchText;
        private boolean onlyAvailableNow;

        // Builder pattern for easy construction
        public static class Builder {
            private TutorSearchCriteria criteria = new TutorSearchCriteria();

            public Builder withSubject(String subjectId) {
                criteria.subjectId = subjectId;
                return this;
            }

            public Builder withPriceRange(Double min, Double max) {
                criteria.minPrice = min;
                criteria.maxPrice = max;
                return this;
            }

            public Builder withMinRating(Double rating) {
                criteria.minRating = rating;
                return this;
            }

            public Builder withAvailability(LocalDateTime from, LocalDateTime to) {
                criteria.availableFrom = from;
                criteria.availableTo = to;
                return this;
            }

            public Builder withSearchText(String text) {
                criteria.searchText = text;
                return this;
            }

            public Builder onlyAvailableNow(boolean available) {
                criteria.onlyAvailableNow = available;
                return this;
            }

            public TutorSearchCriteria build() {
                return criteria;
            }
        }

        // Getters
        public String getSubjectId() {
            return subjectId;
        }

        public Double getMinPrice() {
            return minPrice;
        }

        public Double getMaxPrice() {
            return maxPrice;
        }

        public Double getMinRating() {
            return minRating;
        }

        public LocalDateTime getAvailableFrom() {
            return availableFrom;
        }

        public LocalDateTime getAvailableTo() {
            return availableTo;
        }

        public String getSearchText() {
            return searchText;
        }

        public boolean isOnlyAvailableNow() {
            return onlyAvailableNow;
        }
    }

    public List<TutorSearchResponse> searchTutors(TutorSearchCriteria criteria) throws Exception {
        // Start with all tutors
        List<Tutor> tutors = tutorService.findAll();

        // Apply filters
        tutors = filterBySubject(tutors, criteria.getSubjectId());
        tutors = filterByPrice(tutors, criteria.getMinPrice(), criteria.getMaxPrice());
        tutors = filterByRating(tutors, criteria.getMinRating());
        tutors = filterBySearchText(tutors, criteria.getSearchText());
        tutors = filterByAvailability(tutors, criteria);

        // Convert to response DTOs
        return tutors.stream()
                .map(this::enrichTutor)
                .collect(Collectors.toList());
    }

    private List<Tutor> filterBySubject(List<Tutor> tutors, String subjectId) throws Exception {
        if (subjectId == null)
            return tutors;

        Subject subject = subjectService.findById(subjectId);
        return tutors.stream()
                .filter(t -> t.getSubjects().contains(subject))
                .collect(Collectors.toList());
    }

    private List<Tutor> filterByPrice(List<Tutor> tutors, Double minPrice, Double maxPrice) {
        return tutors.stream()
                .filter(t -> (minPrice == null || t.getHourlyRate() >= minPrice))
                .filter(t -> (maxPrice == null || t.getHourlyRate() <= maxPrice))
                .collect(Collectors.toList());
    }

    private List<Tutor> filterByRating(List<Tutor> tutors, Double minRating) {
        if (minRating == null)
            return tutors;

        return tutors.stream()
                .filter(t -> {
                    double avgRating = calculateAverageRating(t);
                    return avgRating >= minRating;
                })
                .collect(Collectors.toList());
    }

    private List<Tutor> filterBySearchText(List<Tutor> tutors, String searchText) {
        if (searchText == null || searchText.trim().isEmpty())
            return tutors;

        String search = searchText.toLowerCase();
        return tutors.stream()
                .filter(t -> t.getName().toLowerCase().contains(search) ||
                        t.getDescription().toLowerCase().contains(search) ||
                        t.getSubjects().stream()
                                .anyMatch(s -> s.getName().toLowerCase().contains(search)))
                .collect(Collectors.toList());
    }

    private List<Tutor> filterByAvailability(List<Tutor> tutors, TutorSearchCriteria criteria) {
        if (!criteria.isOnlyAvailableNow() &&
                criteria.getAvailableFrom() == null &&
                criteria.getAvailableTo() == null) {
            return tutors;
        }

        LocalDateTime checkFrom = criteria.isOnlyAvailableNow() ? LocalDateTime.now() : criteria.getAvailableFrom();
        LocalDateTime checkTo = criteria.isOnlyAvailableNow() ? LocalDateTime.now().plusHours(1)
                : criteria.getAvailableTo();

        if (checkFrom == null || checkTo == null)
            return tutors;

        ZoneId studentTimeZone = ZoneId.systemDefault(); // Should get from logged-in user
        ZonedDateTime from = checkFrom.atZone(studentTimeZone);
        ZonedDateTime to = checkTo.atZone(studentTimeZone);

        List<String> tutorIds = tutors.stream()
                .map(Tutor::getId)
                .collect(Collectors.toList());

        List<String> availableIds = availabilityService.findAvailableTutors(tutorIds, from, to, studentTimeZone);

        return tutors.stream()
                .filter(t -> availableIds.contains(t.getId()))
                .collect(Collectors.toList());
    }

    private TutorSearchResponse enrichTutor(Tutor tutor) {
        TutorSearchResponse response = new TutorSearchResponse();
        response.setId(tutor.getId());
        response.setName(tutor.getName());
        response.setEmail(tutor.getEmail());
        response.setHourlyRate(tutor.getHourlyRate());
        response.setDescription(tutor.getDescription());

        double avgRating = calculateAverageRating(tutor);
        response.setAverageRating(avgRating);
        response.setTotalReviews(tutor.getReviewsReceived().size());

        response.setSubjects(tutor.getSubjects().stream()
                .map(dtoMapper::toSubjectResponse)
                .collect(Collectors.toList()));

        if (tutor.getProfilePictureId() != null) {
            response.setProfilePictureUrl("/api/files/" + tutor.getProfilePictureId());
        }

        // Check current availability
        response.setCurrentlyAvailable(checkIfCurrentlyAvailable(tutor));

        return response;
    }

    private double calculateAverageRating(Tutor tutor) {
        if (tutor.getReviewsReceived().isEmpty())
            return 0.0;

        return tutor.getReviewsReceived().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private boolean checkIfCurrentlyAvailable(Tutor tutor) {
        try {
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime from = now.atZone(ZoneId.systemDefault());
            ZonedDateTime to = from.plusMinutes(30);

            return availabilityService.isAvailable(tutor.getId(), from, to, ZoneId.systemDefault());
        } catch (Exception e) {
            return false;
        }
    }
}