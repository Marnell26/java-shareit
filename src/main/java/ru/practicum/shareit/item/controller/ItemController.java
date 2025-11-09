package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(X_SHARER_USER_ID) @Positive Long ownerId,
                           @RequestBody @Valid ItemDto item) {
        return itemService.addItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) @Positive Long ownerId,
                              @PathVariable @Positive Long itemId,
                              @RequestBody @Valid ItemDto item) {
        return itemService.updateItem(ownerId, itemId, item);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(X_SHARER_USER_ID) @Positive Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
