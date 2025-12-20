package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.strategy.BookingStrategy;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private List<BookingStrategy> bookingStrategies;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;
    private BookingDto bookingDto;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(now.plusDays(1));
        bookingCreateDto.setEnd(now.plusDays(2));
        bookingCreateDto.setItemId(1L);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        bookingDto.setStatus(BookingStatus.WAITING);

    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(bookingCreateDto, item, booker)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(1L, bookingCreateDto);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBookingWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(999L, bookingCreateDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWhenItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, bookingCreateDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(1L, bookingCreateDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWhenOverlappingBookingExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndStatusAndStartLessThanAndEndGreaterThan(
                eq(1L), eq(BookingStatus.APPROVED), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(1L, bookingCreateDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingTest() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.updateBooking(2L, 1L, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBooking_ShouldReject_WhenOwnerAndValid() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.updateBooking(2L, 1L, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBookingWhenBookingNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(2L, 999L, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingWhenNotOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.updateBooking(999L, 1L, true));
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
    }

    @Test
    void getBookingByIdWhenBookingNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 999L));
    }

    @Test
    void getUserBookingsTest() {
        BookingStrategy strategy = mock(BookingStrategy.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingStrategies.stream()).thenReturn(Stream.of(strategy));
        when(strategy.getState()).thenReturn(BookingState.CURRENT);
        when(strategy.getBookings(1L, false)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getUserBookings(1L, BookingState.CURRENT);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingDto, result.getFirst());
    }

    @Test
    void getUserBookingsWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(999L, BookingState.ALL));
    }

    @Test
    void getOwnerBookingsTest() {
        BookingStrategy strategy = mock(BookingStrategy.class);
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingStrategies.stream()).thenReturn(Stream.of(strategy));
        when(strategy.getState()).thenReturn(BookingState.PAST);
        when(strategy.getBookings(2L, true)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getOwnerBookings(2L, BookingState.PAST);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingDto, result.getFirst());
    }

    @Test
    void getOwnerBookingsWhenOwnerNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings(999L, BookingState.ALL));
    }
}
