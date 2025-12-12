package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "item", source = "item"),
            @Mapping(target = "booker", source = "booker"),
            @Mapping(target = "status", constant = "WAITING")
    })
    Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker);

    BookingDto toBookingDto(Booking booking);

    @Mappings({
            @Mapping(target = "start", source = "booking.start"),
            @Mapping(target = "end", source = "booking.end")
    })
    BookingItemDto toBookingItemDto(Booking booking);

}
