package com.tutoringplatform.dto.response.info;

public class SubjectInfo {
    private String id;
    private String name;
    private int tutorCount;
    private double averagePrice;

    // All getters and setters
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

    public int getTutorCount() {
        return tutorCount;
    }

    public void setTutorCount(int tutorCount) {
        this.tutorCount = tutorCount;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }
}