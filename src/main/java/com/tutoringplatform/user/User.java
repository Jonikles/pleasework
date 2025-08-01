package com.tutoringplatform.user;

import java.time.ZoneId;
import java.util.UUID;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected UserType userType;
    protected String timeZoneId;
    protected String profilePictureId;

    public User(String name, String email, String password, UserType userType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.timeZoneId = ZoneId.systemDefault().getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public ZoneId getTimeZone() {
        return ZoneId.of(timeZoneId);
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZoneId = timeZone.getId();
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
        this.profilePictureId = profilePictureId;
    }
}