package com.tutoringplatform.user.tutor.exceptions;

public class TutorTeachesSubjectException extends TutorException {
    private final String tutorId;
    private final String subjectId;

    public TutorTeachesSubjectException(String tutorId, String subjectId) {
        super("TUTOR_TEACHES_SUBJECT", String.format("Tutor %s already teaches this subject: %s", tutorId, subjectId));
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
