package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper,
            UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        availableCheck(item, bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingCreateDto, item, booker));
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long userId, Long bookingId, boolean approved) {
        Booking updatedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        ownerCheck(updatedBooking, userId);
        updatedBooking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking booking = bookingRepository.save(updatedBooking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookingList = switch (state) {
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDate(userId,
                    currentTime, currentTime);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndDateBeforeOrderByEndDate(userId, currentTime);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartDateAfterOrderByStartDate(userId, currentTime);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDate(userId, BookingState.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDate(userId, BookingState.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDate(userId);
        };
        return bookingList.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    @Transactional
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        return bookingRepository.findAllByItem_OwnerId(ownerId).stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    private void availableCheck(Item item, LocalDateTime start, LocalDateTime end) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        if (bookingRepository.existsByItemIdAndStatusAndStartLessThanAndEndGreaterThan(item.getId(),
                BookingStatus.APPROVED, start, end)) {
            throw new ValidationException();
        }
    }

    private void ownerCheck(Booking booking, Long userId) {
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ForbiddenException("Изменить статус бронирования может только владелец вещи");
        }
    }

}
