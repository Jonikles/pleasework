package com.tutoringplatform.subject.exceptions;

import com.tutoringplatform.shared.exceptions.TutoringPlatformException;

public class SubjectExistsException extends TutoringPlatformException {
    private final String name;

    public SubjectExistsException(String name) {
        super("SUBJECT_EXISTS", String.format("Subject %s already exists", name));
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
