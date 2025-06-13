// FILE: src/main/java/com/tutoringplatform/dto/response/SubjectResponse.java
package com.tutoringplatform.dto.response;

public class SubjectResponse {
    private String id;
    private String name;
    private String category;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}