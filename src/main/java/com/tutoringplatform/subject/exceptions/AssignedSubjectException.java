package com.tutoringplatform.subject.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public class AssignedSubjectException extends TutoringPlatformException {
    private final String id;

    public AssignedSubjectException(String id) {
        super("ASSIGNED_SUBJECT", String.format("Subject %s is assigned to tutors", id));
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
