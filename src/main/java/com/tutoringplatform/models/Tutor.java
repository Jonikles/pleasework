package com.tutoringplatform.models;

import java.util.*;
import java.time.LocalDateTime;

public class Tutor extends User {
    private List<Subject> subjects;
    private double hourlyRate;
    private String description;
    private List<Review> reviewsReceived;
    private Map<String, List<Integer>> availability;
    private double earnings;

    public Tutor(String name, String email, String password, double hourlyRate, String description) {
        super(name, email, password, UserType.TUTOR);
        this.subjects = new ArrayList<>();
        this.hourlyRate = hourlyRate;
        this.description = description;
        this.reviewsReceived = new ArrayList<>();
        this.availability = new HashMap<>();
        this.earnings = 0.0;
        initializeAvailability();
    }

    private void initializeAvailability() {
        String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        for (String day : days) {
            availability.put(day, new ArrayList<>());
        }
    }

    public void addAvailability(String day, int hour) {
        if (hour >= 0 && hour <= 23 && availability.containsKey(day)) {
            if (!availability.get(day).contains(hour)) {
                availability.get(day).add(hour);
                Collections.sort(availability.get(day));
            }
        }
    }

    public void addAvailability(LocalDateTime dateTime, int durationHours) {
        String day = dateTime.getDayOfWeek().toString().substring(0, 1)
                + dateTime.getDayOfWeek().toString().substring(1).toLowerCase();
        int startHour = dateTime.getHour();

        // Add each hour in the duration
        for (int i = 0; i < durationHours; i++) {
            int hour = startHour + i;
            if (hour < 24) {
                addAvailability(day, hour);
            }
        }
    }

    public void removeAvailability(String day, int hour) {
        if (availability.containsKey(day)) {
            availability.get(day).remove(Integer.valueOf(hour));
        }
    }

    public boolean isAvailable(String day, int hour) {
        return availability.containsKey(day) && availability.get(day).contains(hour);
    }

    public double getAverageRating() {
        if (reviewsReceived.isEmpty())
            return 0.0;
        return reviewsReceived.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void addSubject(Subject subject) {
        if (!subjects.contains(subject))
            subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);
    }

    public void printUnavailableTimes() {
        System.out.println("\n=== Unavailable Times for " + this.name + " ===");
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

        for (String day : days) {
            List<Integer> availableHours = availability.get(day);
            List<Integer> unavailableHours = new ArrayList<>();

            for (int hour = 0; hour < 24; hour++) {
                if (!availableHours.contains(hour)) {
                    unavailableHours.add(hour);
                }
            }

            if (!unavailableHours.isEmpty()) {
                System.out.println(day + ":");
                System.out.print("  Unavailable: ");

                int start = unavailableHours.get(0);
                int end = start;

                for (int i = 1; i <= unavailableHours.size(); i++) {
                    if (i < unavailableHours.size() && unavailableHours.get(i) == end + 1) {
                        end = unavailableHours.get(i);
                    } else {
                        if (start == end) {
                            System.out.print(String.format("%02d:00", start) + " ");
                        } else {
                            System.out.print(String.format("%02d:00", start) + "-" + String.format("%02d:00", end) + " ");
                        }

                        if (i < unavailableHours.size()) {
                            start = unavailableHours.get(i);
                            end = start;
                        }
                    }
                }
                System.out.println();
            }
        }
        System.out.println("================================\n");
    }

    public void addEarnings(double amount) {
        this.earnings += amount;
    }
    
    public List<Review> getReviewsReceived() {
        return reviewsReceived;
    }

    public void addReview(Review review) {
        reviewsReceived.add(review);
    }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, List<Integer>> getAvailability() { return availability; }
    public void setAvailability(Map<String, List<Integer>> availability) { this.availability = availability; }

    public double getEarnings() { return earnings; }
    public void setEarnings(double earnings) { this.earnings = earnings; }

}