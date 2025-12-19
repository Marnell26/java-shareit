package ru.practicum.shareit.user.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.constant.GeneratedMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserDto userDto, @MappingTarget User updatedUser);
}
