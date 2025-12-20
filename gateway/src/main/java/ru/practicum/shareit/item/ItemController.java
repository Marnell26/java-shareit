package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static ru.practicum.shareit.constant.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @RequestBody @Valid CreateItemDto createItemDto) {
        return itemClient.addItem(ownerId, createItemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemDto updateItemDto) {
        return itemClient.updateItem(ownerId, itemId, updateItemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        return itemClient.getItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid CreateCommentDto createCommentDto) {
        return itemClient.addComment(userId, itemId, createCommentDto);
    }


}
