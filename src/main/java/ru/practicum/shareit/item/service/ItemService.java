package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long ownerId, ItemCreateDto itemCreateDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    List<ItemFullDto> getItemsByOwner(Long ownerId);

    ItemFullDto getItem(Long itemId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);

}
