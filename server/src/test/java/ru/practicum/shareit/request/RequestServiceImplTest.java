package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private User otherUser;
    private ItemRequest itemRequest;
    private ItemCreateRequestDto createDto;
    private ItemRequestDto requestDto;
    private Item item;
    private ItemShortDto itemShortDto;
    private ItemRequestDtoWithItemsList requestDtoWithItems;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("user2");
        otherUser.setEmail("user2@mail.ru");

        createDto = new ItemCreateRequestDto();
        createDto.setDescription("Need an item");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need an item");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(now);

        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need an item");
        requestDto.setCreated(now);

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);

        itemShortDto = new ItemShortDto();
        itemShortDto.setId(1L);
        itemShortDto.setName("Item");

        requestDtoWithItems = new ItemRequestDtoWithItemsList();
        requestDtoWithItems.setId(1L);
        requestDtoWithItems.setDescription("Need an item");
        requestDtoWithItems.setCreated(now);
        requestDtoWithItems.setItems(List.of(itemShortDto));

    }

    @Test
    void addRequestTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(any(ItemCreateRequestDto.class), any(User.class), any(LocalDateTime.class)))
                .thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(requestDto);

        ItemRequestDto result = itemRequestService.addRequest(1L, createDto);

        assertNotNull(result);
        assertEquals(requestDto.getId(), result.getId());
        assertEquals(requestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void addRequestWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(999L, createDto));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getOwnRequestsTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestDtoWithItemsList(any(), any())).thenReturn(requestDtoWithItems);

        List<ItemRequestDtoWithItemsList> result = itemRequestService.getOwnRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestDtoWithItems.getId(), result.getFirst().getId());
        assertEquals(1, result.getFirst().getItems().size());
    }

    @Test
    void getOwnRequestsWhenUserHasNoRequests() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of());

        List<ItemRequestDtoWithItemsList> result = itemRequestService.getOwnRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOwnRequestsWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getOwnRequests(999L));
    }

    @Test
    void getAllRequestsWhenOtherRequestsExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdNot(1L)).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestDtoWithItemsList(any(), any())).thenReturn(requestDtoWithItems);

        List<ItemRequestDtoWithItemsList> result = itemRequestService.getAllRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestDtoWithItems.getId(), result.getFirst().getId());
        assertEquals(1, result.getFirst().getItems().size());
    }

    @Test
    void getAllRequestsWhenNoOtherRequests() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdNot(1L)).thenReturn(List.of());

        List<ItemRequestDtoWithItemsList> result = itemRequestService.getAllRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequestsWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(999L));
    }

    @Test
    void getRequestByIdTest() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestDtoWithItemsList(any(), any())).thenReturn(requestDtoWithItems);

        ItemRequestDtoWithItemsList result = itemRequestService.getRequestById(1L);

        assertNotNull(result);
        assertEquals(requestDtoWithItems.getId(), result.getId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getRequestByIdWhenNoItemsLinked() {
        ItemRequestDtoWithItemsList itemRequestDto = new ItemRequestDtoWithItemsList();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Need an item");
        itemRequestDto.setCreated(now);
        itemRequestDto.setItems(List.of());

        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of());
        when(itemRequestMapper.toItemRequestDtoWithItemsList(any(), eq(List.of()))).thenReturn(itemRequestDto);

        ItemRequestDtoWithItemsList result = itemRequestService.getRequestById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getRequestByIdWhenRequestNotFound() {
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L));
    }

    @Test
    void getRequestByIdWhenRequestIsNull() {
        when(itemRequestRepository.findById(null)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> itemRequestService.getRequestById(null));
    }
}
