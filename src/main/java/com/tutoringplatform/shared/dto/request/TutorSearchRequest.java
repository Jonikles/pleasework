package com.tutoringplatform.shared.dto.request;

import java.time.LocalDateTime;

public class TutorSearchRequest {
    private String subjectId;
    private double minPrice;
    private double maxPrice;
    private double minRating;
    private Boolean availableNow;
    private LocalDateTime availableDateTime;
    private String searchText;
    private String sortBy; // "PRICE_LOW", "PRICE_HIGH", "RATING", "REVIEWS"
    private Integer page;
    private Integer pageSize;

    // All getters and setters
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinRating() {
        return minRating;
    }

    public void setMinRating(double minRating) {
        this.minRating = minRating;
    }

    public Boolean getAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(Boolean availableNow) {
        this.availableNow = availableNow;
    }

    public LocalDateTime getAvailableDateTime() {
        return availableDateTime;
    }

    public void setAvailableDateTime(LocalDateTime availableDateTime) {
        this.availableDateTime = availableDateTime;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}