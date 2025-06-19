package com.tutoringplatform.user.tutor.exceptions;

public class TutorHasBookingsException extends TutorException {
    private final String tutorId;
    private final String subjectId;

    public TutorHasBookingsException(String tutorId, String subjectId) {
        super("TUTOR_HAS_BOOKINGS", String.format("Tutor %s has bookings for subject %s.", tutorId, subjectId));
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
