package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Override
    public ItemDto addItem(Long ownerId, ItemDto item) {
        return null;
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto item) {
        return null;
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return List.of();
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return null;
    }

    @Override
    public List<ItemDto> search(String text) {
        return List.of();
    }
}
