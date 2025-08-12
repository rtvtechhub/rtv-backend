package com.rtvnewsnetwork.common.exception;


import java.time.Instant;

public class ExceptionResponse {

    private final String message;
    private final String details;
    private final Instant timestamp;

    public ExceptionResponse(String message, String details) {
        this.message = message;
        this.details = details;
        timestamp = Instant.now();
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
