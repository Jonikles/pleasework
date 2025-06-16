package com.tutoringplatform.dto.response;

public class ValueResponse<T> {
    private T value;
    private String message;

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
