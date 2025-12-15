package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(Long userId, ItemCreateRequestDto itemCreateRequestDto);

    List<ItemRequestDto> getOwnRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto getRequestById(Long requestId);

}
