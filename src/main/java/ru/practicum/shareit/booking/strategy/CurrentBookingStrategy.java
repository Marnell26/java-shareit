package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CurrentBookingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;
    private final LocalDateTime now = LocalDateTime.now();

    @Override
    public List<Booking> getBookings(Long userId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
        } else {
            return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
        }
    }

    @Override
    public BookingState getState() {
        return BookingState.CURRENT;
    }
}
