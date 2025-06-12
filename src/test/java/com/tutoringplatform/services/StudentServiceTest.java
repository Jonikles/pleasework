package com.tutoringplatform.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tutoringplatform.models.Student;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private IStudentRepository studentRepository;

    private StudentService studentService;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository);
        testStudent = new Student("John Doe", "john@student.com", "password123");
    }

    @Test
    @DisplayName("Should register new student successfully")
    void testRegisterStudent() throws Exception {
        when(studentRepository.findByEmail("john@student.com")).thenReturn(null);

        studentService.register(testStudent);

        verify(studentRepository).save(testStudent);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegisterStudentEmailExists() {
        when(studentRepository.findByEmail("john@student.com")).thenReturn(testStudent);

        Student newStudent = new Student("Another John", "john@student.com", "password");
        assertThrows(Exception.class, () -> {
            studentService.register(newStudent);
        }, "Email already exists");
    }

    @Test
    @DisplayName("Should add funds successfully")
    void testAddFunds() throws Exception {
        when(studentRepository.findById("student123")).thenReturn(testStudent);
        double initialBalance = testStudent.getBalance();

        studentService.addFunds("student123", 100.50);

        assertEquals(initialBalance + 100.50, testStudent.getBalance());
        verify(studentRepository).update(testStudent);
    }

    @Test
    @DisplayName("Should throw exception for negative amount")
    void testAddFundsNegativeAmount() {
        assertThrows(Exception.class, () -> {
            studentService.addFunds("student123", -50.0);
        }, "Amount must be positive");
    }

    @Test
    @DisplayName("Should throw exception for zero amount")
    void testAddFundsZeroAmount() {
        assertThrows(Exception.class, () -> {
            studentService.addFunds("student123", 0.0);
        }, "Amount must be positive");
    }

    @Test
    @DisplayName("Should throw exception when student not found for add funds")
    void testAddFundsStudentNotFound() {
        when(studentRepository.findById("nonexistent")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            studentService.addFunds("nonexistent", 100.0);
        }, "User not found with id: nonexistent");
    }

    @Test
    @DisplayName("Should search students by name")
    void testSearchByName() {
        Student student1 = new Student("John Doe", "john1@test.com", "pass");
        Student student2 = new Student("John Smith", "john2@test.com", "pass");
        List<Student> expectedStudents = Arrays.asList(student1, student2);

        when(studentRepository.findByNameContaining("John")).thenReturn(expectedStudents);

        List<Student> result = studentService.searchByName("John");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getName().contains("John")));
    }

    @Test
    @DisplayName("Should find student by ID")
    void testFindById() throws Exception {
        when(studentRepository.findById("student123")).thenReturn(testStudent);

        Student found = studentService.findById("student123");

        assertNotNull(found);
        assertEquals(testStudent.getId(), found.getId());
    }

    @Test
    @DisplayName("Should throw exception when student not found by ID")
    void testFindByIdNotFound() {
        when(studentRepository.findById("nonexistent")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            studentService.findById("nonexistent");
        }, "User not found with id: nonexistent");
    }

    @Test
    @DisplayName("Should update student")
    void testUpdateStudent() throws Exception {
        when(studentRepository.findById(testStudent.getId())).thenReturn(testStudent);
        testStudent.setName("Updated Name");

        studentService.update(testStudent);

        verify(studentRepository).update(testStudent);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent student")
    void testUpdateNonExistentStudent() {
        when(studentRepository.findById(testStudent.getId())).thenReturn(null);

        assertThrows(Exception.class, () -> {
            studentService.update(testStudent);
        }, "User not found");
    }
}