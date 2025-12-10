package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request.id", source = "requestId")
    Item toItem(ItemDto itemDto);

    @Mappings({
            @Mapping(target = "ownerId", source = "owner.id"),
            @Mapping(target = "requestId", source = "requestId")
    })
    ItemDto toItemDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", source = "request")
    void updateItem(ItemDto itemDto, @MappingTarget Item updatedItem);
}
