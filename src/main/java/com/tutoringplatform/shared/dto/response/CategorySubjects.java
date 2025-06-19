package com.tutoringplatform.shared.dto.response;

import com.tutoringplatform.shared.dto.response.info.SubjectInfo;

import java.util.List;

public class CategorySubjects {
    private String category;
    private List<SubjectInfo> subjects;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<SubjectInfo> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectInfo> subjects) {
        this.subjects = subjects;
    }
}
