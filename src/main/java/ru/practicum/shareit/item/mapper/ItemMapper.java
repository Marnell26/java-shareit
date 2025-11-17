package ru.practicum.shareit.item.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItem(ItemDto itemDto, @MappingTarget Item updatedItem);
}
