package ru.practicum.shareit.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    private final String errorDescription;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private List<ValidationError> errors;

    public ErrorResponse(String errorDescription, List<ValidationError> errors) {
        this.errorDescription = errorDescription;
        this.errors = errors;
    }

    public ErrorResponse(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
