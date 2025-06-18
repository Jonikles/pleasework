package com.tutoringplatform.user.tutor.exceptions;

public class TutorTeachesSubjectException extends Exception {
    private String tutorId;
    private String subjectId;

    public TutorTeachesSubjectException(String tutorId, String subjectId) {
        super(String.format("Tutor already teaches this subject: %s", subjectId));
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
