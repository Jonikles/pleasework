package com.tutoringplatform.user.student.exceptions;

public class StudentNotFoundException extends StudentException {
    public StudentNotFoundException(String studentId) {
        super(studentId, "Student not found: " + studentId);
    }
}
