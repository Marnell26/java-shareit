package ru.practicum.shareit.request.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.constant.GeneratedMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemsList;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "requestor", source = "user"),
            @Mapping(target = "created", source = "now")
    })
    ItemRequest toItemRequest(ItemCreateRequestDto createDto, User user, LocalDateTime now);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mappings({
            @Mapping(target = "id", source = "itemRequest.id"),
            @Mapping(target = "items", source = "items")
    })
    ItemRequestDtoWithItemsList toItemRequestDtoWithItemsList(ItemRequest itemRequest, List<ItemShortDto> items);

}
