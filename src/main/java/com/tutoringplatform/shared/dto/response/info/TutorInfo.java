package com.tutoringplatform.shared.dto.response.info;

public class TutorInfo extends UserInfo {
    private double hourlyRate;

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}