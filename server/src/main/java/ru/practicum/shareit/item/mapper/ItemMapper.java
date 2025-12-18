package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "itemCreateDto.name"),
            @Mapping(target = "description", source = "itemCreateDto.description"),
            @Mapping(target = "owner", source = "owner"),
            @Mapping(target = "request", source = "itemRequest")
    })
    Item toItem(ItemCreateDto itemCreateDto, User owner, ItemRequest itemRequest);

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

    @Mapping(target = "ownerId", source = "item.owner.id")
    ItemShortDto toItemShortDto(Item item);
}
