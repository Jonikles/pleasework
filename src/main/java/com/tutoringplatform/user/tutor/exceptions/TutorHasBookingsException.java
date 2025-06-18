package com.tutoringplatform.user.tutor.exceptions;

public class TutorHasBookingsException extends Exception {
    private final String tutorId;
    private final String subjectId;

    public TutorHasBookingsException(String tutorId, String subjectId) {
        super("Tutor " + tutorId + " has bookings for subject " + subjectId + ".");
        this.tutorId = tutorId;
        this.subjectId = subjectId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public String getSubjectId() {
        return subjectId;
    }
}
