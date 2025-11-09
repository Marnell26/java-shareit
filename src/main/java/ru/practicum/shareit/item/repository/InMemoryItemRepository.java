package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public class InMemoryItemRepository implements ItemRepository {


    @Override
    public Item addItem(Long ownerId, Item item) {
        return null;
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, Item item) {
        return null;
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return List.of();
    }

    @Override
    public Item getItem(Long itemId) {
        return null;
    }

    @Override
    public List<Item> search(String text) {
        return List.of();
    }
}
