package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemsList;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constant.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody ItemCreateRequestDto itemCreateRequestDto) {
        return itemRequestService.addRequest(userId, itemCreateRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithItemsList> getOwnRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItemsList> getAllRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItemsList getRequestById(@PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }

}
