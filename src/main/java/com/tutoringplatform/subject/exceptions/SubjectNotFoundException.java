package com.tutoringplatform.subject.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public class SubjectNotFoundException extends TutoringPlatformException {
    private final String id;

    public SubjectNotFoundException(String id) {
        super("SUBJECT_NOT_FOUND", String.format("Subject %s not found", id));
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
