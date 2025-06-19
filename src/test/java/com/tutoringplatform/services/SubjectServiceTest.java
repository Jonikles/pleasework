package com.tutoringplatform.services;

import com.tutoringplatform.shared.dto.request.CreateSubjectRequest;
import com.tutoringplatform.shared.dto.response.SubjectListResponse;
import com.tutoringplatform.shared.dto.response.SubjectResponse;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.subject.ISubjectRepository;
import com.tutoringplatform.subject.SubjectService;
import com.tutoringplatform.subject.exceptions.*;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.subject.Subject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private ISubjectRepository subjectRepository;
    @Mock
    private TutorService tutorService;
    @Mock
    private DTOMapper dtoMapper;

    private SubjectService subjectService;

    @BeforeEach
    void setUp() {
        subjectService = new SubjectService(subjectRepository, tutorService, dtoMapper);
    }

    @Test
    void createSubject_Success() throws Exception {
        // Arrange
        CreateSubjectRequest request = new CreateSubjectRequest();
        request.setName("Mathematics");
        request.setCategory("Science");

        SubjectResponse expectedResponse = new SubjectResponse();
        expectedResponse.setName("Mathematics");
        expectedResponse.setCategory("Science");

        when(subjectRepository.findByName("Mathematics")).thenReturn(null);
        when(dtoMapper.toSubjectResponse(any(Subject.class))).thenReturn(expectedResponse);

        // Act
        SubjectResponse result = subjectService.createSubject(request);

        // Assert
        assertNotNull(result);
        assertEquals("Mathematics", result.getName());
        assertEquals("Science", result.getCategory());
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void createSubject_AlreadyExists_ThrowsException() {
        // Arrange
        CreateSubjectRequest request = new CreateSubjectRequest();
        request.setName("Mathematics");
        request.setCategory("Science");

        Subject existingSubject = new Subject("Mathematics", "Science");
        when(subjectRepository.findByName("Mathematics")).thenReturn(existingSubject);

        // Act & Assert
        assertThrows(SubjectExistsException.class, () -> subjectService.createSubject(request));
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void deleteSubject_Success() throws Exception {
        // Arrange
        String subjectId = "subject123";
        Subject subject = new Subject("Math", "Science");
        subject.setId(subjectId);

        when(subjectRepository.findById(subjectId)).thenReturn(subject);
        when(tutorService.findBySubject(subject)).thenReturn(Collections.emptyList());

        // Act
        subjectService.deleteSubject(subjectId);

        // Assert
        verify(subjectRepository).delete(subjectId);
    }

    @Test
    void deleteSubject_AssignedToTutors_ThrowsException() throws Exception {
        // Arrange
        String subjectId = "subject123";
        Subject subject = new Subject("Math", "Science");
        subject.setId(subjectId);

        Tutor tutor = new Tutor("John", "john@email.com", "password", 50.0, "Math tutor");
        tutor.getSubjects().add(subject);

        when(subjectRepository.findById(subjectId)).thenReturn(subject);
        when(tutorService.findBySubject(subject)).thenReturn(Arrays.asList(tutor));

        // Act & Assert
        assertThrows(AssignedSubjectException.class, () -> subjectService.deleteSubject(subjectId));
        verify(subjectRepository, never()).delete(anyString());
    }

    @Test
    void deleteSubject_NotFound_ThrowsException() {
        // Arrange
        String subjectId = "nonexistent";
        when(subjectRepository.findById(subjectId)).thenReturn(null);

        // Act & Assert
        assertThrows(SubjectNotFoundException.class, () -> subjectService.deleteSubject(subjectId));
    }

    @Test
    void findById_Success() throws Exception {
        // Arrange
        String subjectId = "subject123";
        Subject subject = new Subject("Math", "Science");
        subject.setId(subjectId);

        when(subjectRepository.findById(subjectId)).thenReturn(subject);

        // Act
        Subject result = subjectService.findById(subjectId);

        // Assert
        assertNotNull(result);
        assertEquals(subjectId, result.getId());
        assertEquals("Math", result.getName());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        // Arrange
        String subjectId = "nonexistent";
        when(subjectRepository.findById(subjectId)).thenReturn(null);

        // Act & Assert
        assertThrows(SubjectNotFoundException.class, () -> subjectService.findById(subjectId));
    }

    @Test
    void getAllSubjects_Success() {
        // Arrange
        Subject math = new Subject("Math", "Science");
        Subject physics = new Subject("Physics", "Science");
        Subject english = new Subject("English", "Language");

        List<Subject> allSubjects = Arrays.asList(math, physics, english);
        SubjectListResponse expectedResponse = new SubjectListResponse();

        when(subjectRepository.findAll()).thenReturn(allSubjects);
        when(tutorService.findBySubject(any())).thenReturn(Collections.emptyList());
        when(dtoMapper.toSubjectListResponse(anyList())).thenReturn(expectedResponse);

        // Act
        SubjectListResponse result = subjectService.getAllSubjects();

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(dtoMapper).toSubjectListResponse(anyList());
    }

    @Test
    void getAvailableSubjectsForTutor_Success() throws Exception {
        // Arrange
        String tutorId = "tutor123";

        Subject math = new Subject("Math", "Science");
        math.setId("math123");
        Subject physics = new Subject("Physics", "Science");
        physics.setId("physics123");
        Subject english = new Subject("English", "Language");
        english.setId("english123");

        Tutor tutor = new Tutor("John", "john@email.com", "password", 50.0, "Teacher");
        tutor.setId(tutorId);
        tutor.getSubjects().add(math);

        SubjectResponse physicsResponse = new SubjectResponse();
        SubjectResponse englishResponse = new SubjectResponse();

        when(tutorService.findById(tutorId)).thenReturn(tutor);
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(math, physics, english));
        when(dtoMapper.toSubjectResponse(physics)).thenReturn(physicsResponse);
        when(dtoMapper.toSubjectResponse(english)).thenReturn(englishResponse);

        // Act
        List<SubjectResponse> result = subjectService.getAvailableSubjectsForTutor(tutorId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Only physics and english should be available
        verify(dtoMapper, never()).toSubjectResponse(math); // Math should not be mapped
    }

    @Test
    void getAvailableSubjectsForTutor_TutorNotFound_ThrowsException() throws Exception {
        // Arrange
        String tutorId = "nonexistent";
        when(tutorService.findById(tutorId)).thenThrow(new UserNotFoundException(tutorId));

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> subjectService.getAvailableSubjectsForTutor(tutorId));
    }

    @Test
    @SuppressWarnings("unused")
    void groupSubjectsByCategory_Success() {
        // Arrange
        Subject math = new Subject("Math", "Science");
        Subject physics = new Subject("Physics", "Science");
        Subject english = new Subject("English", "Language");

        Tutor tutor1 = new Tutor("John", "john@email.com", "password", 50.0, "Teacher");
        Tutor tutor2 = new Tutor("Jane", "jane@email.com", "password", 60.0, "Teacher");

        when(subjectRepository.findAll()).thenReturn(Arrays.asList(math, physics, english));
        when(tutorService.findBySubject(math)).thenReturn(Arrays.asList(tutor1, tutor2));
        when(tutorService.findBySubject(physics)).thenReturn(Arrays.asList(tutor1));
        when(tutorService.findBySubject(english)).thenReturn(Collections.emptyList());
        
        // Act
        SubjectListResponse result = subjectService.getAllSubjectsByCategory();

        // Assert
        verify(tutorService, times(3)).findBySubject(any(Subject.class));
    }

    @Test
    void getSubjectById_Success() throws Exception {
        // Arrange
        String subjectId = "subject123";
        Subject subject = new Subject("Math", "Science");
        subject.setId(subjectId);

        SubjectResponse expectedResponse = new SubjectResponse();
        expectedResponse.setId(subjectId);
        expectedResponse.setName("Math");
        expectedResponse.setCategory("Science");

        when(subjectRepository.findById(subjectId)).thenReturn(subject);
        when(dtoMapper.toSubjectResponse(subject)).thenReturn(expectedResponse);

        // Act
        SubjectResponse result = subjectService.getSubjectById(subjectId);

        // Assert
        assertNotNull(result);
        assertEquals(subjectId, result.getId());
        assertEquals("Math", result.getName());
        assertEquals("Science", result.getCategory());
    }

    @Test
    void findAll_Success() {
        // Arrange
        List<Subject> subjects = Arrays.asList(
                new Subject("Math", "Science"),
                new Subject("Physics", "Science"));

        when(subjectRepository.findAll()).thenReturn(subjects);

        // Act
        List<Subject> result = subjectService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByCategory_Success() {
        // Arrange
        String category = "Science";
        List<Subject> scienceSubjects = Arrays.asList(
                new Subject("Math", "Science"),
                new Subject("Physics", "Science"));

        when(subjectRepository.findByCategory(category)).thenReturn(scienceSubjects);

        // Act
        List<Subject> result = subjectService.findByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getCategory().equals("Science")));
    }
}