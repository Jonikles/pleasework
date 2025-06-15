package com.tutoringplatform.dto.response;

import java.util.List;

import com.tutoringplatform.dto.response.info.TutorSearchResultInfo;

public class TutorSearchResultsResponse {
    private List<TutorSearchResultInfo> results;
    private int totalCount;
    private SearchFilters filters;

    // All getters and setters
    public List<TutorSearchResultInfo> getResults() {
        return results;
    }

    public void setResults(List<TutorSearchResultInfo> results) {
        this.results = results;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public SearchFilters getFilters() {
        return filters;
    }

    public void setFilters(SearchFilters filters) {
        this.filters = filters;
    }
}
