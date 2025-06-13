package com.tutoringplatform.dto.response;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String userType;

    public UserResponse(String id, String name, String email, String userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }
}