package com.tutoringplatform.review.reviewExceptions;

public class NoCompletedBookingsException extends ReviewException {
    private final String studentId;
    private final String tutorId;

    public NoCompletedBookingsException(String studentId, String tutorId) {
        super("NO_COMPLETED_BOOKINGS", String.format("Student %s has no completed bookings with tutor %s", studentId, tutorId));
        this.studentId = studentId;
        this.tutorId = tutorId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getTutorId() {
        return tutorId;
    }
}
