package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    List<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto getItem(Long itemId);

    List<ItemDto> search(String text);
}
