package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemsList;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRequestMapper itemRequestMapper,
            UserRepository userRepository, ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestMapper = itemRequestMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public ItemRequestDto addRequest(Long userId, ItemCreateRequestDto createDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();
        ItemRequest request = itemRequestRepository.save(itemRequestMapper.toItemRequest(createDto, requestor, now));

        return itemRequestMapper.toItemRequestDto(request);
    }

    @Override
    @Transactional
    public List<ItemRequestDtoWithItemsList> getOwnRequests(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> itemRequestMapper.toItemRequestDtoWithItemsList(request, getRequestItems(request)))
                .toList();
    }

    @Override
    @Transactional
    public List<ItemRequestDtoWithItemsList> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdNot(userId);
        return requests.stream()
                .map(request -> itemRequestMapper.toItemRequestDtoWithItemsList(request, getRequestItems(request)))
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDtoWithItemsList getRequestById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        return itemRequestMapper.toItemRequestDtoWithItemsList(request, getRequestItems(request));
    }

    private List<ItemShortDto> getRequestItems(ItemRequest request) {
        List<Item> itemss = itemRepository.findAllByRequestId(request.getId());
        System.out.println("Результат запроса: " + itemss);
        List<ItemShortDto> items = itemRepository.findAllByRequestId(request.getId()).stream()
                .map(itemMapper::toItemShortDto)
                .toList();
        System.out.println(items);
        return items;
    }
}
