// FILE: src/main/java/com/tutoringplatform/dto/request/SubjectRequest.java
package com.tutoringplatform.dto.request;

public class SubjectRequest {
    private String name;
    private String category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}