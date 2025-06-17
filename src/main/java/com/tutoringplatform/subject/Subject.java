package com.tutoringplatform.subject;

import java.util.UUID;

public class Subject {
    private String id;
    private String name;
    private String category;

    public Subject(String name, String category) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Subject subject = (Subject) obj;
        return id.equals(subject.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}