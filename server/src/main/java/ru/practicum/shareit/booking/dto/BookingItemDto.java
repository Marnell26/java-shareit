package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingItemDto {
    private LocalDateTime start;
    private LocalDateTime end;
}
