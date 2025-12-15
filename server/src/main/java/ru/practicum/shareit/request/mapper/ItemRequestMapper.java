package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "requestor", source = "user"),
            @Mapping(target = "created", source = "now")
    })
    ItemRequest toItemRequest(ItemCreateRequestDto createDto, User user, LocalDateTime now);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

}
