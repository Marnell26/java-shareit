package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
}
