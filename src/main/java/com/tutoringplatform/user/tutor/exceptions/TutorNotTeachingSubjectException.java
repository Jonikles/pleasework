package com.tutoringplatform.user.tutor.exceptions;

public class TutorNotTeachingSubjectException extends TutorException {
    private final String tutorId;
    private final String subjectId;

    public TutorNotTeachingSubjectException(String tutorId, String subjectId) {
        super("TUTOR_NOT_TEACHING_SUBJECT", "Tutor " + tutorId + " is not teaching subject " + subjectId + ".");
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
