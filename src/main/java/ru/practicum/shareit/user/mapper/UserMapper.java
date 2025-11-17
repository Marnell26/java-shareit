package ru.practicum.shareit.user.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserDto userDto, @MappingTarget User updatedUser);
}
