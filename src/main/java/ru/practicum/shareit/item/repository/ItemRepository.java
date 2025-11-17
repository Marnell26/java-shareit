package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Long ownerId, Item item);

    Item updateItem(Long itemId, Item item);

    List<Item> getItemsByOwner(Long ownerId);

    Item getItem(Long itemId);

    List<Item> search(String text);

}
