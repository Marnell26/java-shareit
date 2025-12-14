package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mappings({
            @Mapping(target = "name", source = "itemCreateDto.name"),
            @Mapping(target = "owner", source = "owner"),
            @Mapping(target = "request", ignore = true)
    })
    Item toItem(ItemCreateDto itemCreateDto, User owner);

    ItemDto toItemDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    void updateItem(ItemDto itemDto, @MappingTarget Item updatedItem);

    @Mappings({
            @Mapping(target = "lastBooking", source = "last"),
            @Mapping(target = "nextBooking", source = "next"),
            @Mapping(target = "comments", source = "comments")
    })
    ItemFullDto toItemFullDto(Item item, BookingItemDto last, BookingItemDto next, List<CommentDto> comments);
}
