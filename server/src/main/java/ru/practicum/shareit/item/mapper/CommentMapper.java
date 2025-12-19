package ru.practicum.shareit.item.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.constant.GeneratedMapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "text", source = "commentCreateDto.text"),
            @Mapping(target = "item", source = "item"),
            @Mapping(target = "author", source = "author"),
            @Mapping(target = "created", source = "created")
    })
    Comment toComment(CommentCreateDto commentCreateDto, Item item, User author, LocalDateTime created);

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

}
