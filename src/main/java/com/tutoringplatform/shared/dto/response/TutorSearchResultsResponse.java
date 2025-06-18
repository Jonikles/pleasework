package com.tutoringplatform.shared.dto.response;

import com.tutoringplatform.shared.dto.response.info.TutorSearchResultInfo;

import java.util.List;

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
