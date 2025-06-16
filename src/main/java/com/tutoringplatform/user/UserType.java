package com.tutoringplatform.models;

public enum UserType {
    STUDENT("Student"),
    TUTOR("Tutor");
    // Future: ADMIN("Admin")
    
    private final String displayName;
    
    UserType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}