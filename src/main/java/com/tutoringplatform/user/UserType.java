package com.tutoringplatform.user;

public enum UserType {
    STUDENT("Student"),
    TUTOR("Tutor");
    
    private final String displayName;
    
    UserType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}