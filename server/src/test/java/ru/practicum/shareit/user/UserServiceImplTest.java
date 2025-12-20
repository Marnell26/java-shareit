package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();
    }

    @Test
    void addUserTest() {

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.addUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addUserWhenEmailIsNotUnique() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.addUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserTest() {
        User updatedUser = User.builder()
                .id(1L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        UserDto updateDto = UserDto.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("user2@mail.ru")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(UserDto.builder()
                .id(1L)
                .name("user2")
                .email("user2@mail.ru")
                .build());

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("user2", result.getName());
        assertEquals("user2@mail.ru", result.getEmail());
        verify(userMapper).updateUser(updateDto, user);
    }

    @Test
    void updateNonExistUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(999L, userDto));
    }

    @Test
    void updateUserWhenEmailIsNotUnique() {
        User anotherUser = User.builder()
                .id(2L)
                .name("user")
                .email("user@mail.ru")
                .build();

        UserDto updateDto = UserDto.builder()
                .email("user@mail.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("user@mail.ru")).thenReturn(Optional.of(anotherUser));

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDto));
        verify(userMapper, never()).updateUser(any(), any());
    }

    @Test
    void updateUserWhenEmailIsNull() {
        UserDto updateDto = UserDto.builder()
                .name("user2")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("user1", result.getName());
        assertEquals("user1@mail.ru", result.getEmail());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByIdWhenUserNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getAllUsersTest() {
        List<User> users = List.of(user);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto, result.getFirst());
    }

    @Test
    void getUsersWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteNonExistUser() {
        assertDoesNotThrow(() -> userService.deleteUser(999L));
        verify(userRepository, times(1)).deleteById(999L);
    }
}