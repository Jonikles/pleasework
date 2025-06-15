package com.tutoringplatform.dto.response;

import java.util.List;

public class SubjectListResponse {
    private List<CategorySubjects> subjects;

    public List<CategorySubjects> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<CategorySubjects> subjects) {
        this.subjects = subjects;
    }
}