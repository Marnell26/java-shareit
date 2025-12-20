package ru.practicum.shareit.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GatewayErrorResponse {
    private final String error;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private List<ValidationError> errors;

    public GatewayErrorResponse(String error, List<ValidationError> errors) {
        this.error = error;
        this.errors = errors;
    }

    public GatewayErrorResponse(String error) {
        this.error = error;
        ;
    }
}
