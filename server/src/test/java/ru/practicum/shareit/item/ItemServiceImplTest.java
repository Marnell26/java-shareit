package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;
    private ItemRequest itemRequest;
    private ItemCreateDto itemCreateDto;
    private ItemDto itemDto;
    private Comment comment;
    private CommentCreateDto commentCreateDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@mail.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Booker")
                .email("booker@mail.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Description1")
                .available(true)
                .owner(owner)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need an item")
                .requestor(booker)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("New Item");
        itemCreateDto.setDescription("New Description");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(1L);

        itemDto = ItemDto.builder()
                .id(1L)
                .name("UpdatedItem")
                .description("UpdatedDescription")
                .available(false)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Good")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now().minusHours(1))
                .build();

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Good");

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(4))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void addItemTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemMapper.toItem(itemCreateDto, owner, itemRequest)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.addItem(1L, itemCreateDto);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void addItemWhenOwnerNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(999L, itemCreateDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItemWhenRequestNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        ItemCreateDto itemCreateDto2 = new ItemCreateDto();
        itemCreateDto2.setName("Item2");
        itemCreateDto2.setDescription("Description");
        itemCreateDto2.setAvailable(true);
        itemCreateDto2.setRequestId(999L);

        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemCreateDto2));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemTest() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.updateItem(1L, 1L, itemDto);

        assertNotNull(result);
        assertEquals("UpdatedItem", result.getName());
        assertEquals("UpdatedDescription", result.getDescription());
        assertFalse(result.getAvailable());
        verify(itemMapper).updateItem(itemDto, item);
        verify(itemRepository).save(item);
    }

    @Test
    void updateItemWhenItemNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 999L, itemDto));
        verify(itemMapper, never()).updateItem(any(), any());
    }

    @Test
    void updateItemWhenNotOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, 1L, itemDto));
        verify(itemMapper, never()).updateItem(any(), any());
    }

    @Test
    void getItemsByOwnerTest() {
        BookingItemDto lastBooking = new BookingItemDto();
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        BookingItemDto nextBooking = new BookingItemDto();
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Good item!");
        commentDto.setAuthorName("Booker");
        commentDto.setCreated(LocalDateTime.now().minusHours(1));

        List<CommentDto> comments = List.of(commentDto);

        ItemFullDto itemFullDto = new ItemFullDto();
        itemFullDto.setId(1L);
        itemFullDto.setName("Item");
        itemFullDto.setDescription("Description");
        itemFullDto.setAvailable(true);
        itemFullDto.setLastBooking(lastBooking);
        itemFullDto.setNextBooking(nextBooking);
        itemFullDto.setComments(comments);

        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));

        when(bookingRepository.findTopByItemIdAndEndBeforeOrderByEndDesc(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.findTopByItemIdAndStartAfterOrderByStartDesc(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        when(bookingMapper.toBookingItemDto(any(Booking.class)))
                .thenReturn(lastBooking)
                .thenReturn(nextBooking);

        when(commentRepository.findAllByItemIdOrderByCreatedDesc(1L)).thenReturn(List.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        when(itemMapper.toItemFullDto(
                eq(item),
                eq(lastBooking),
                eq(nextBooking),
                eq(comments)
        )).thenReturn(itemFullDto);

        List<ItemFullDto> result = itemService.getItemsByOwner(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemFullDto, result.getFirst());
    }

    @Test
    void getItemTest() {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Good");
        commentDto.setCreated(LocalDateTime.now().minusHours(1));

        List<Comment> comments = List.of(comment);
        List<CommentDto> commentsDto = List.of(commentDto);

        ItemFullDto itemFullDto = new ItemFullDto();
        itemFullDto.setId(1L);
        itemFullDto.setName("Item");
        itemFullDto.setDescription("Description");
        itemFullDto.setAvailable(true);
        itemFullDto.setComments(commentsDto);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(1L)).thenReturn(comments);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        when(itemMapper.toItemFullDto(eq(item), isNull(), isNull(), eq(commentsDto))).thenReturn(itemFullDto);

        ItemFullDto result = itemService.getItem(1L);

        assertNotNull(result);
        assertEquals("Item", result.getName());
        assertEquals(commentsDto, result.getComments());
    }

    @Test
    void getItemWhenItemNotExist() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(999L));
    }

    @Test
    void searchTest() {
        when(itemRepository.search("Test")).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.search("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.getFirst());
    }

    @Test
    void searchWhenTextIsBlank() {
        List<ItemDto> result = itemService.search("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Good");
        commentDto.setAuthorName("Booker");
        commentDto.setCreated(LocalDateTime.now().minusSeconds(1));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(1L), eq(2L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        when(commentMapper.toComment(eq(commentCreateDto), eq(item), eq(booker), any(LocalDateTime.class)))
                .thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = itemService.addComment(2L, 1L, commentCreateDto);

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getAuthorName(), result.getAuthorName());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addCommentWhenUserNeverBookedItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> itemService.addComment(2L, 1L, commentCreateDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentWhenItemNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(2L, 999L, commentCreateDto));
    }

    @Test
    void addCommentWhenUserNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(999L, 1L, commentCreateDto));
    }

}
