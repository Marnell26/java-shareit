package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemsList;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(Long userId, ItemCreateRequestDto itemCreateRequestDto);

    List<ItemRequestDtoWithItemsList> getOwnRequests(Long userId);

    List<ItemRequestDtoWithItemsList> getAllRequests(Long userId);

    ItemRequestDtoWithItemsList getRequestById(Long requestId);

}
