package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        return List.of();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        return List.of();
    }
}
