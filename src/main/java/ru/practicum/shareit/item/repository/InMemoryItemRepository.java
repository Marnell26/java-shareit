package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public Item addItem(Long ownerId, Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        List<Item> itemList = new ArrayList<>(items.values());
        return itemList.stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .toList();
    }

    @Override
    public Item getItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Предмет не найден");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> search(String text) {
        String finalText = text.toLowerCase();
        List<Item> itemList = new ArrayList<>(items.values());
        return itemList.stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(finalText)
                        || item.getDescription().toLowerCase().contains(finalText))
                .toList();
    }

    private Long generateId() {
        return id++;
    }
}
