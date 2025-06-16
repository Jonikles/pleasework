package com.tutoringplatform.search;

import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.booking.IBookingRepository;
import com.tutoringplatform.review.IReviewRepository;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.shared.dto.request.TutorSearchRequest;
import com.tutoringplatform.shared.dto.response.*;
import com.tutoringplatform.shared.dto.response.info.TutorSearchResultInfo;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.ISubjectRepository;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.ITutorRepository;
import com.tutoringplatform.user.Tutor;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.user.availability.availability.TutorAvailability;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ITutorRepository tutorRepository;
    private final ISubjectRepository subjectRepository;
    private final IReviewRepository reviewRepository;
    private final IBookingRepository bookingRepository;
    private final AvailabilityService availabilityService;
    private final DTOMapper dtoMapper;

    @Autowired
    public SearchService(
            ITutorRepository tutorRepository,
            ISubjectRepository subjectRepository,
            IReviewRepository reviewRepository,
            IBookingRepository bookingRepository,
            AvailabilityService availabilityService,
            DTOMapper dtoMapper) {
        this.tutorRepository = tutorRepository;
        this.subjectRepository = subjectRepository;
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.availabilityService = availabilityService;
        this.dtoMapper = dtoMapper;
    }

    public TutorSearchResultsResponse searchTutors(TutorSearchRequest request) throws Exception {
        // Start with all tutors
        List<Tutor> tutors = tutorRepository.findAll();

        // Apply filters
        tutors = applyFilters(tutors, request);

        // Sort results
        tutors = sortResults(tutors, request.getSortBy());

        // Apply pagination
        int page = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        int totalCount = tutors.size();

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, tutors.size());

        List<Tutor> paginatedTutors = startIndex < tutors.size() ? tutors.subList(startIndex, endIndex)
                : new ArrayList<>();

        // Convert to search results
        List<TutorSearchResultInfo> results = new ArrayList<>();
        for (Tutor tutor : paginatedTutors) {
            TutorSearchResultInfo result = buildSearchResult(tutor);
            results.add(result);
        }

        // Build filters for response
        SearchFilters appliedFilters = new SearchFilters();
        appliedFilters.setSubjectId(request.getSubjectId());
        appliedFilters.setMinPrice(request.getMinPrice());
        appliedFilters.setMaxPrice(request.getMaxPrice());
        appliedFilters.setMinRating(request.getMinRating());
        appliedFilters.setSortBy(request.getSortBy());

        return dtoMapper.toTutorSearchResultsResponse(results, totalCount, appliedFilters);
    }

    private List<Tutor> applyFilters(List<Tutor> tutors, TutorSearchRequest request) throws Exception {
        // Filter by subject
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId());
            if (subject != null) {
                tutors = tutors.stream()
                        .filter(t -> t.getSubjects().contains(subject))
                        .collect(Collectors.toList());
            }
        }

        // Filter by price range
        if (request.getMinPrice() > 0) {
            tutors = tutors.stream()
                    .filter(t -> t.getHourlyRate() >= request.getMinPrice())
                    .collect(Collectors.toList());
        }

        if (request.getMaxPrice() > 0) {
            tutors = tutors.stream()
                    .filter(t -> t.getHourlyRate() <= request.getMaxPrice())
                    .collect(Collectors.toList());
        }

        // Filter by minimum rating
        if (request.getMinRating() > 0) {
            tutors = tutors.stream()
                    .filter(t -> {
                        double avgRating = calculateAverageRating(t.getId());
                        return avgRating >= request.getMinRating();
                    })
                    .collect(Collectors.toList());
        }

        // Filter by search text
        if (request.getSearchText() != null && !request.getSearchText().trim().isEmpty()) {
            String searchLower = request.getSearchText().toLowerCase();
            tutors = tutors.stream()
                    .filter(t -> t.getName().toLowerCase().contains(searchLower) ||
                            t.getDescription().toLowerCase().contains(searchLower) ||
                            t.getSubjects().stream()
                                    .anyMatch(s -> s.getName().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }

        // Filter by availability
        if (request.getAvailableNow() != null && request.getAvailableNow()) {
            tutors = filterByCurrentAvailability(tutors);
        } else if (request.getAvailableDateTime() != null) {
            tutors = filterBySpecificAvailability(tutors, request.getAvailableDateTime());
        }

        return tutors;
    }

    private List<Tutor> filterByCurrentAvailability(List<Tutor> tutors) {
        ZoneId userTimeZone = ZoneId.systemDefault(); // Should get from user context
        ZonedDateTime now = ZonedDateTime.now(userTimeZone);
        ZonedDateTime oneHourLater = now.plusHours(1);

        return tutors.stream()
                .filter(tutor -> {
                    try {
                        return availabilityService.isAvailable(
                                tutor.getId(), now, oneHourLater, userTimeZone);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private List<Tutor> filterBySpecificAvailability(List<Tutor> tutors, LocalDateTime dateTime) {
        ZoneId userTimeZone = ZoneId.systemDefault(); // Should get from user context
        ZonedDateTime start = dateTime.atZone(userTimeZone);
        ZonedDateTime end = start.plusHours(1); // Default 1 hour session

        return tutors.stream()
                .filter(tutor -> {
                    try {
                        return availabilityService.isAvailable(
                                tutor.getId(), start, end, userTimeZone);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private List<Tutor> sortResults(List<Tutor> tutors, String sortBy) {
        if (sortBy == null) {
            sortBy = "RATING"; // Default sort
        }

        switch (sortBy) {
            case "PRICE_LOW":
                tutors.sort(Comparator.comparing(Tutor::getHourlyRate));
                break;
            case "PRICE_HIGH":
                tutors.sort(Comparator.comparing(Tutor::getHourlyRate).reversed());
                break;
            case "RATING":
                tutors.sort((a, b) -> {
                    double ratingA = calculateAverageRating(a.getId());
                    double ratingB = calculateAverageRating(b.getId());
                    return Double.compare(ratingB, ratingA); // Descending
                });
                break;
            case "REVIEWS":
                tutors.sort((a, b) -> {
                    int reviewsA = reviewRepository.getTutorReviews(a.getId()).size();
                    int reviewsB = reviewRepository.getTutorReviews(b.getId()).size();
                    return Integer.compare(reviewsB, reviewsA); // Descending
                });
                break;
        }

        return tutors;
    }

    private TutorSearchResultInfo buildSearchResult(Tutor tutor) throws Exception {
        // Calculate average rating
        double averageRating = calculateAverageRating(tutor.getId());

        // Get review count
        int reviewCount = reviewRepository.getTutorReviews(tutor.getId()).size();

        // Create short description (first 100 chars)
        String shortDescription = tutor.getDescription();
        if (shortDescription.length() > 100) {
            shortDescription = shortDescription.substring(0, 97) + "...";
        }

        // Find next available slot
        LocalDateTime nextAvailable = findNextAvailableSlot(tutor.getId());

        return dtoMapper.toTutorSearchResult(
                tutor,
                averageRating,
                reviewCount,
                shortDescription,
                nextAvailable);
    }

    private double calculateAverageRating(String tutorId) {
        List<Review> reviews = reviewRepository.getTutorReviews(tutorId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private LocalDateTime findNextAvailableSlot(String tutorId) throws Exception {
        // Get tutor's availability
        TutorAvailability availability = availabilityService.getAvailability(tutorId);
        if (availability.getRecurringSlots().isEmpty()) {
            return null;
        }

        // Get existing bookings
        List<Booking> bookings = bookingRepository.findByTutorId(tutorId).stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .filter(b -> b.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        // Find next available slot (simplified - would need more complex logic)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkTime = now.plusHours(1); // Start checking from 1 hour from now

        // Check next 7 days
        for (int i = 0; i < 168; i++) { // 168 hours = 7 days
            LocalDateTime slotTime = checkTime.plusHours(i);
            ZonedDateTime slotStart = slotTime.atZone(availability.getTimeZone());
            ZonedDateTime slotEnd = slotStart.plusHours(1);

            // Check if this slot is available
            boolean isAvailable = availabilityService.isAvailable(
                    tutorId, slotStart, slotEnd, availability.getTimeZone());

            if (isAvailable) {
                // Check if not already booked
                boolean isBooked = bookings.stream()
                        .anyMatch(b -> {
                            LocalDateTime bookingEnd = b.getDateTime().plusHours(b.getDurationHours());
                            return !slotTime.isAfter(bookingEnd) && !slotTime.plusHours(1).isBefore(b.getDateTime());
                        });

                if (!isBooked) {
                    return slotTime;
                }
            }
        }

        return null;
    }
}