package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RejectedBookingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long userId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingState.REJECTED);
        } else {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingState.REJECTED);
        }
    }

    @Override
    public BookingState getState() {
        return BookingState.REJECTED;
    }
}
