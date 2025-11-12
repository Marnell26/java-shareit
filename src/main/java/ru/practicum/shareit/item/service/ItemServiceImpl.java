package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        itemDto.setOwner(userRepository.getUserById(ownerId));
        Item item = itemRepository.addItem(ownerId, itemMapper.toItem(itemDto));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item updatedItem = itemRepository.getItem(itemId);
        if (!Objects.equals(ownerId, updatedItem.getOwner().getId())) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
        }
        if (itemDto.getName() == null) {
            itemDto.setName(updatedItem.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(updatedItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(updatedItem.getAvailable());
        }
        itemDto.setOwner(updatedItem.getOwner());
        itemDto.setRequest(updatedItem.getRequest());
        Item item = itemRepository.updateItem(itemId, itemMapper.toItem(itemDto));
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.getItemsByOwner(ownerId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.getItem(itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}
