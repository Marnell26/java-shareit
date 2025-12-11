package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_OwnerId(long userId);

    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDate(Long bookerId, LocalDateTime start
            , LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndDateBeforeOrderByEndDate(Long bookerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartDateAfterOrderByStartDate(Long bookerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDate(Long bookerId, BookingState status);

    List<Booking> findByBookerIdOrderByStartDate(Long bookerId);

    boolean existsByItemIdAndStatusAndStartLessThanAndEndGreaterThan(
            Long itemId, BookingStatus status, LocalDateTime start, LocalDateTime end);

}
