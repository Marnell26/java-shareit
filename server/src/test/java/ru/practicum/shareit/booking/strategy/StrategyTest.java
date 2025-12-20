package ru.practicum.shareit.booking.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StrategyTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AllBookingStrategy allBookingStrategy;
    @InjectMocks
    private CurrentBookingStrategy currentBookingStrategy;
    @InjectMocks
    private FutureBookingStrategy futureBookingStrategy;
    @InjectMocks
    private PastBookingStrategy pastBookingStrategy;
    @InjectMocks
    private RejectedBookingStrategy rejectedBookingStrategy;
    @InjectMocks
    private WaitingBookingStrategy waitingBookingStrategy;

    private final Long userId = 1L;

    private List<Booking> bookingsAsBooker;
    private List<Booking> bookingsAsOwner;

    @BeforeEach
    void setUp() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(java.time.LocalDateTime.now().minusDays(2));
        booking1.setEnd(java.time.LocalDateTime.now().minusDays(1));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(java.time.LocalDateTime.now().plusDays(1));
        booking2.setEnd(java.time.LocalDateTime.now().plusDays(2));

        bookingsAsBooker = List.of(booking2, booking1);
        bookingsAsOwner = List.of(booking1, booking2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"All", "Current", "Future", "Past", "Rejected", "Waiting"})
    void getStateTest(String status) {
        BookingState state;
        BookingState expectedState = switch (status) {
            case "All" -> {
                state = allBookingStrategy.getState();
                yield BookingState.ALL;
            }
            case "Current" -> {
                state = currentBookingStrategy.getState();
                yield BookingState.CURRENT;
            }
            case "Future" -> {
                state = futureBookingStrategy.getState();
                yield BookingState.FUTURE;
            }
            case "Past" -> {
                state = pastBookingStrategy.getState();
                yield BookingState.PAST;
            }
            case "Rejected" -> {
                state = rejectedBookingStrategy.getState();
                yield BookingState.REJECTED;
            }
            default -> {
                state = waitingBookingStrategy.getState();
                yield BookingState.WAITING;
            }
        };

        assertEquals(expectedState, state);
    }

    @ParameterizedTest
    @ValueSource(strings = {"All", "Current", "Future", "Past", "Rejected", "Waiting"})
    void getBookingsWhenIsOwnerTrue(String status) {
        List<Booking> result;
        switch (status) {
            case "All" -> {
                when(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId)).thenReturn(bookingsAsOwner);
                result = allBookingStrategy.getBookings(userId, true);
            }
            case "Current" -> {
                when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                        .thenReturn(bookingsAsOwner);
                result = currentBookingStrategy.getBookings(userId, true);
            }
            case "Future" -> {
                when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        eq(userId), any(LocalDateTime.class)))
                        .thenReturn(bookingsAsOwner);
                result = futureBookingStrategy.getBookings(userId, true);
            }
            case "Past" -> {
                when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        eq(userId), any(LocalDateTime.class)))
                        .thenReturn(bookingsAsOwner);
                result = pastBookingStrategy.getBookings(userId, true);
            }
            case "Rejected" -> {
                when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        eq(userId), eq(BookingState.REJECTED)))
                        .thenReturn(bookingsAsOwner);
                result = rejectedBookingStrategy.getBookings(userId, true);
            }
            default -> {
                when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        eq(userId), eq(BookingState.WAITING)))
                        .thenReturn(bookingsAsOwner);
                result = waitingBookingStrategy.getBookings(userId, true);
            }
        }

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingsAsOwner, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"All", "Current", "Future", "Past", "Rejected", "Waiting"})
    void getBookingsWhenIsOwnerFalse(String status) {
        List<Booking> result;
        switch (status) {
            case "All" -> {
                when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(bookingsAsBooker);
                result = allBookingStrategy.getBookings(userId, false);
            }
            case "Current" -> {
                when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                        .thenReturn(bookingsAsBooker);
                result = currentBookingStrategy.getBookings(userId, false);
            }
            case "Future" -> {
                when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        eq(userId), any(LocalDateTime.class)))
                        .thenReturn(bookingsAsBooker);
                result = futureBookingStrategy.getBookings(userId, false);
            }
            case "Past" -> {
                when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                        eq(userId), any(LocalDateTime.class)))
                        .thenReturn(bookingsAsBooker);
                result = pastBookingStrategy.getBookings(userId, false);
            }
            case "Rejected" -> {
                when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        eq(userId), eq(BookingState.REJECTED)))
                        .thenReturn(bookingsAsBooker);
                result = rejectedBookingStrategy.getBookings(userId, false);
            }
            default -> {
                when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        eq(userId), eq(BookingState.WAITING)))
                        .thenReturn(bookingsAsBooker);
                result = waitingBookingStrategy.getBookings(userId, false);
            }
        }

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingsAsBooker, result);
    }
}
