package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        validateUniqueEmail(userDto.getEmail());
        User user = userRepository.addUser(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User updatedUser = userRepository.getUserById(id);
        if (userDto.getName() == null) {
            userDto.setName(updatedUser.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(updatedUser.getEmail());
        }
        validateUniqueEmail(userDto.getEmail());
        User user = userRepository.updateUser(id, userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getUserById(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    private void validateUniqueEmail(String email) {
        userRepository.getUsers().stream()
                .map(User::getEmail)
                .filter(s -> s.equals(email))
                .findFirst()
                .ifPresent(s -> {throw new ConflictException("Email уже используется");});
    }
}
