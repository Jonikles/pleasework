// FILE: src/main/java/com/tutoringplatform/dto/request/AvailabilityRequest.java
package com.tutoringplatform.dto.request;

public class AvailabilityRequest {
    private String day;
    private int hour;
    private boolean add;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }
}