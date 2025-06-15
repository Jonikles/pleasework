package com.tutoringplatform.dto.response;

public class ValueResponse<T> {
    private T value;
    private String message;

    public ValueResponse(T value) {
        this.value = value;
    }

    public ValueResponse(T value, String message) {
        this.value = value;
        this.message = message;
    }

    // Getters and setters
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}