package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        return bookingService.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }

}
