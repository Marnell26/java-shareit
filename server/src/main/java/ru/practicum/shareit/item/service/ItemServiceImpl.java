package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, UserRepository userRepository,
            CommentRepository commentRepository, CommentMapper commentMapper, BookingRepository bookingRepository,
            BookingMapper bookingMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional
    public ItemDto addItem(Long ownerId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.save(itemMapper.toItem(itemCreateDto, owner));
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!Objects.equals(ownerId, updatedItem.getOwner().getId())) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
        }
        itemMapper.updateItem(itemDto, updatedItem);
        Item item = itemRepository.save(updatedItem);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public List<ItemFullDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(item -> itemMapper.toItemFullDto(item, getLastBooking(item.getId()),
                        getNextBooking(item.getId()), getComments(item.getId())))
                .toList();
    }

    @Override
    @Transactional
    public ItemFullDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        return itemMapper.toItemFullDto(item, null, null, getComments(itemId));
    }

    private BookingItemDto getLastBooking(Long itemId) {
        Booking lastBooking = bookingRepository.findTopByItemIdAndEndBeforeOrderByEndDesc(itemId,
                LocalDateTime.now()).orElse(null);
        return bookingMapper.toBookingItemDto(lastBooking);
    }

    private BookingItemDto getNextBooking(Long itemId) {
        Booking nextBooking = bookingRepository.findTopByItemIdAndStartAfterOrderByStartDesc(itemId,
                LocalDateTime.now()).orElse(null);
        return bookingMapper.toBookingItemDto(nextBooking);
    }

    private List<CommentDto> getComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();
        if (bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED,
                now).isPresent()) {
            Comment comment = commentRepository.save(commentMapper.toComment(commentCreateDto, item, user, now));
            return commentMapper.toCommentDto(comment);
        } else {
            throw new ValidationException("Оставить отзыв может только пользователь, который брал вещь в аренду");
        }

    }
}
