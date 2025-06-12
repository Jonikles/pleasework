package com.tutoringplatform.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tutoringplatform.models.Review;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private ITutorRepository tutorRepository;

    private TutorService tutorService;
    private Tutor testTutor;

    @BeforeEach
    void setUp() {
        tutorService = new TutorService(tutorRepository);
        testTutor = new Tutor("Jane Smith", "jane@tutor.com", "password123",
                50.0, "Experienced math tutor");
    }

    @Test
    @DisplayName("Should register new tutor successfully")
    void testRegisterTutor() throws Exception {
        when(tutorRepository.findByEmail("jane@tutor.com")).thenReturn(null);

        tutorService.register(testTutor);

        verify(tutorRepository).save(testTutor);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegisterTutorEmailExists() {
        when(tutorRepository.findByEmail("jane@tutor.com")).thenReturn(testTutor);

        Tutor newTutor = new Tutor("Another Jane", "jane@tutor.com", "password", 60.0, "Description");
        assertThrows(Exception.class, () -> {
            tutorService.register(newTutor);
        }, "Email already exists");
    }

    @Test
    @DisplayName("Should throw exception for invalid hourly rate")
    void testRegisterTutorInvalidRate() {
        Tutor invalidTutor = new Tutor("Bob", "bob@tutor.com", "password", -10.0, "Description");
        when(tutorRepository.findByEmail("bob@tutor.com")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            tutorService.register(invalidTutor);
        }, "Hourly rate must be positive");
    }

    @Test
    @DisplayName("Should search tutors by subject")
    void testSearchTutorsBySubject() {
        Subject mathSubject = new Subject("Math", "Mathematics");
        testTutor.addSubject(mathSubject);

        Tutor tutor2 = new Tutor("Bob Wilson", "bob@tutor.com", "password", 60.0, "Physics tutor");
        Subject physicsSubject = new Subject("Physics", "Science");
        tutor2.addSubject(physicsSubject);

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        List<Tutor> mathTutors = tutorService.searchTutors(mathSubject, null, null, null, null, null);

        assertEquals(1, mathTutors.size());
        assertEquals("Jane Smith", mathTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should search tutors by price range")
    void testSearchTutorsByPriceRange() {
        Tutor tutor2 = new Tutor("Bob", "bob@tutor.com", "password", 75.0, "Senior tutor");
        Tutor tutor3 = new Tutor("Alice", "alice@tutor.com", "password", 30.0, "Junior tutor");

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2, tutor3); // 50, 75, 30
        when(tutorRepository.findAll()).thenReturn(allTutors);

        List<Tutor> midRangeTutors = tutorService.searchTutors(null, 40.0, 60.0, null, null, null);

        assertEquals(1, midRangeTutors.size());
        assertEquals(50.0, midRangeTutors.get(0).getHourlyRate());
    }

    @Test
    @DisplayName("Should search tutors by minimum rating")
    void testSearchTutorsByRating() {
        testTutor.addReview(
                new Review("student1", testTutor.getId(), "booking1", 5, "Excellent"));
        testTutor.addReview(
                new Review("student2", testTutor.getId(), "booking2", 4, "Good"));

        Tutor lowRatedTutor = new Tutor("Bob", "bob@tutor.com", "password", 40.0, "New tutor");
        lowRatedTutor.addReview(
                new Review("student3", lowRatedTutor.getId(), "booking3", 2, "Poor"));

        List<Tutor> allTutors = Arrays.asList(testTutor, lowRatedTutor);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        List<Tutor> highRatedTutors = tutorService.searchTutors(null, null, null, 4.0, null, null);

        assertEquals(1, highRatedTutors.size());
        assertEquals(4.5, highRatedTutors.get(0).getAverageRating());
    }

    @Test
    @DisplayName("Should search tutors by availability")
    void testSearchTutorsByAvailability() {
        testTutor.addAvailability("Monday", 14);
        testTutor.addAvailability("Monday", 15);

        Tutor tutor2 = new Tutor("Bob", "bob@tutor.com", "password", 60.0, "Evening tutor");
        tutor2.addAvailability("Monday", 18);
        tutor2.addAvailability("Monday", 19);

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        List<Tutor> afternoonTutors = tutorService.searchTutors(null, null, null, null, "Monday", 14);

        assertEquals(1, afternoonTutors.size());
        assertEquals("Jane Smith", afternoonTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should combine multiple search filters")
    void testSearchTutorsMultipleFilters() {
        Subject mathSubject = new Subject("Math", "Mathematics");
        testTutor.addSubject(mathSubject);
        testTutor.addAvailability("Monday", 14);

        Tutor tutor2 = new Tutor("Bob", "bob@tutor.com", "password", 100.0, "Expensive tutor");
        tutor2.addSubject(mathSubject);
        tutor2.addAvailability("Monday", 14);

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        List<Tutor> filteredTutors = tutorService.searchTutors(mathSubject, null, 60.0, null, "Monday", 14);

        assertEquals(1, filteredTutors.size());
        assertEquals("Jane Smith", filteredTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should update availability successfully")
    void testUpdateAvailability() throws Exception {
        when(tutorRepository.findById("tutor123")).thenReturn(testTutor);

        tutorService.updateAvailability("tutor123", "Monday", 14, true);

        assertTrue(testTutor.isAvailable("Monday", 14));
        verify(tutorRepository).update(testTutor);

        tutorService.updateAvailability("tutor123", "Monday", 14, false);

        assertFalse(testTutor.isAvailable("Monday", 14));
        verify(tutorRepository, times(2)).update(testTutor);
    }

    @Test
    @DisplayName("Should throw exception when tutor not found for update")
    void testUpdateAvailabilityTutorNotFound() {
        when(tutorRepository.findById("invalid")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            tutorService.updateAvailability("invalid", "Monday", 14, true);
        }, "User not found with id: invalid");
    }

    @Test
    @DisplayName("Should find tutor by ID")
    void testFindById() throws Exception {
        when(tutorRepository.findById("tutor123")).thenReturn(testTutor);

        Tutor found = tutorService.findById("tutor123");

        assertNotNull(found);
        assertEquals(testTutor.getId(), found.getId());
    }

    @Test
    @DisplayName("Should return empty list when no tutors match criteria")
    void testSearchNoMatches() {
        when(tutorRepository.findAll()).thenReturn(Arrays.asList(testTutor));

        List<Tutor> expensiveTutors = tutorService.searchTutors(null, 100.0, null, null, null, null);

        assertTrue(expensiveTutors.isEmpty());
    }
}