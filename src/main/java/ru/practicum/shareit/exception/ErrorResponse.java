package ru.practicum.shareit.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    private final String errorDescription;
    private final String status;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private List<ValidationError> errors;

    public ErrorResponse(String errorDescription, String status, List<ValidationError> errors) {
        this.errorDescription = errorDescription;
        this.status = status;
        this.errors = errors;
    }

    public ErrorResponse(String errorDescription, String status) {
        this.errorDescription = errorDescription;
        this.status = status;
    }
}
