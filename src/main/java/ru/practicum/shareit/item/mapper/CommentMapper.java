package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment(CommentCreateDto commentCreateDto, Item item, User author, LocalDateTime created);

    CommentDto toCommentDto(Comment comment);

}
