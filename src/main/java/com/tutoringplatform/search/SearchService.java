package com.tutoringplatform.search;

import com.tutoringplatform.user.availability.model.TutorAvailability;
import com.tutoringplatform.booking.Booking;
import com.tutoringplatform.review.ReviewService;
import com.tutoringplatform.review.Review;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.booking.BookingService;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.user.availability.AvailabilityService;
import com.tutoringplatform.shared.dto.request.TutorSearchRequest;
import com.tutoringplatform.shared.dto.response.*;
import com.tutoringplatform.shared.dto.response.info.TutorSearchResultInfo;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.subject.exceptions.SubjectNotFoundException;
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final TutorService tutorService;
    private final SubjectService subjectService;
    private final ReviewService reviewService;
    private final BookingService bookingService;
    private final AvailabilityService availabilityService;
    private final DTOMapper dtoMapper;

    @Autowired
    public SearchService(
            TutorService tutorService,
            SubjectService subjectService,
            ReviewService reviewService,
            BookingService bookingService,
            AvailabilityService availabilityService,
            DTOMapper dtoMapper) {
        this.tutorService = tutorService;
        this.subjectService = subjectService;
        this.reviewService = reviewService;
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
        this.dtoMapper = dtoMapper;
    }

    public TutorSearchResultsResponse searchTutors(TutorSearchRequest request) throws SubjectNotFoundException, NoCompletedBookingsException, UserNotFoundException, PaymentNotFoundException {
        logger.debug("Searching for tutors with request: {}", request);
        List<Tutor> tutors = tutorService.findAll();

        tutors = applyFilters(tutors, request);

        tutors = sortResults(tutors, request.getSortBy());

        int page = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        int totalCount = tutors.size();

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, tutors.size());

        List<Tutor> paginatedTutors = startIndex < tutors.size() ? tutors.subList(startIndex, endIndex)
                : new ArrayList<>();

        List<TutorSearchResultInfo> results = new ArrayList<>();
        for (Tutor tutor : paginatedTutors) {
            TutorSearchResultInfo result = buildSearchResult(tutor);
            results.add(result);
        }

        SearchFilters appliedFilters = new SearchFilters();
        appliedFilters.setSubjectId(request.getSubjectId());
        appliedFilters.setMinPrice(request.getMinPrice());
        appliedFilters.setMaxPrice(request.getMaxPrice());
        appliedFilters.setMinRating(request.getMinRating());
        appliedFilters.setSortBy(request.getSortBy());

        logger.info("found {} tutors, search result {}", results.size(), results);
        return dtoMapper.toTutorSearchResultsResponse(results, totalCount, appliedFilters);
    }

    private List<Tutor> applyFilters(List<Tutor> tutors, TutorSearchRequest request) throws SubjectNotFoundException, NoCompletedBookingsException, UserNotFoundException, PaymentNotFoundException {
        if (request.getSubjectId() != null) {
            Subject subject = subjectService.findById(request.getSubjectId());
            tutors = tutors.stream()
                    .filter(t -> t.getSubjects().contains(subject))
                    .collect(Collectors.toList());
        }

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

        if (request.getMinRating() > 0) {
            Map<Tutor, Double> tutorRatings = new HashMap<>();
            
            for (Tutor tutor : tutors) {
                double avgRating = calculateAverageRating(tutor.getId());
                tutorRatings.put(tutor, avgRating);
            }
            tutors.sort(Comparator.comparing(tutorRatings::get).reversed());
        }        

        if (request.getSearchText() != null && !request.getSearchText().trim().isEmpty()) {
            String searchLower = request.getSearchText().toLowerCase();
            tutors = tutors.stream()
                    .filter(t -> t.getName().toLowerCase().contains(searchLower) ||
                            t.getDescription().toLowerCase().contains(searchLower) ||
                            t.getSubjects().stream()
                                    .anyMatch(s -> s.getName().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }

        if (request.getAvailableNow() != null && request.getAvailableNow()) {
            tutors = filterByCurrentAvailability(tutors);
        } else if (request.getAvailableDateTime() != null) {
            tutors = filterBySpecificAvailability(tutors, request.getAvailableDateTime());
        }

        return tutors;
    }

    private List<Tutor> filterByCurrentAvailability(List<Tutor> tutors) {
        ZoneId userTimeZone = ZoneId.systemDefault();
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
        ZoneId userTimeZone = ZoneId.systemDefault();
        ZonedDateTime start = dateTime.atZone(userTimeZone);
        ZonedDateTime end = start.plusHours(1);

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

    private List<Tutor> sortResults(List<Tutor> tutors, String sortBy) throws NoCompletedBookingsException, UserNotFoundException, PaymentNotFoundException {
        if (sortBy == null) {
            sortBy = "RATING";
        }

        switch (sortBy) {
            case "PRICE_LOW":
                tutors.sort(Comparator.comparing(Tutor::getHourlyRate));
                break;
            case "PRICE_HIGH":
                tutors.sort(Comparator.comparing(Tutor::getHourlyRate).reversed());
                break;
            case "RATING":
                Map<Tutor, Double> tutorRatings = new HashMap<>();
                for (Tutor tutor : tutors) {
                    double rating = calculateAverageRating(tutor.getId());
                    tutorRatings.put(tutor, rating);
                }
                tutors.sort(Comparator.comparing(tutorRatings::get).reversed());
                break;
            case "REVIEWS":
                Map<Tutor, Integer> tutorReviews = new HashMap<>();
                for (Tutor tutor : tutors) {
                    int reviewCount = reviewService.getTutorReviews(tutor.getId()).size();
                    tutorReviews.put(tutor, reviewCount);
                }
                tutors.sort(Comparator.comparing(tutorReviews::get).reversed());
                break;
        }

        return tutors;
    }

    private TutorSearchResultInfo buildSearchResult(Tutor tutor) throws NoCompletedBookingsException, UserNotFoundException, PaymentNotFoundException {
        double averageRating = calculateAverageRating(tutor.getId());

        int reviewCount = reviewService.getTutorReviews(tutor.getId()).size();

        String shortDescription = tutor.getDescription();
        if (shortDescription.length() > 100) {
            shortDescription = shortDescription.substring(0, 97) + "...";
        }

        LocalDateTime nextAvailable = findNextAvailableSlot(tutor.getId());

        return dtoMapper.toTutorSearchResult(
                tutor,
                averageRating,
                reviewCount,
                shortDescription,
                nextAvailable);
    }

    private double calculateAverageRating(String tutorId) throws NoCompletedBookingsException, UserNotFoundException {
        List<Review> reviews = reviewService.getTutorReviews(tutorId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private LocalDateTime findNextAvailableSlot(String tutorId) throws UserNotFoundException, PaymentNotFoundException {
        TutorAvailability availability = availabilityService.getAvailability(tutorId);
        if (availability.getRecurringSlots().isEmpty()) {
            return null;
        }

        List<Booking> bookings = bookingService.getTutorBookingList(tutorId);
        bookings = bookings.stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .filter(b -> b.getDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkTime = now.plusHours(1);

        for (int i = 0; i < 168; i++) {
            LocalDateTime slotTime = checkTime.plusHours(i);
            ZonedDateTime slotStart = slotTime.atZone(availability.getTimeZone());
            ZonedDateTime slotEnd = slotStart.plusHours(1);

            boolean isAvailable = availabilityService.isAvailable(
                    tutorId, slotStart, slotEnd, availability.getTimeZone());

            if (isAvailable) {
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