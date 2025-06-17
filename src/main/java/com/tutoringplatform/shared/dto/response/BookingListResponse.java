package com.tutoringplatform.shared.dto.response;

import java.util.List;

public class BookingListResponse {
    private List<BookingDetailResponse> upcomingBookings;
    private List<BookingDetailResponse> pastBookings;
    private List<BookingDetailResponse> cancelledBookings;

    // All getters and setters
    public List<BookingDetailResponse> getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(List<BookingDetailResponse> upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public List<BookingDetailResponse> getPastBookings() {
        return pastBookings;
    }

    public void setPastBookings(List<BookingDetailResponse> pastBookings) {
        this.pastBookings = pastBookings;
    }

    public List<BookingDetailResponse> getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(List<BookingDetailResponse> cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }
}