package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User addUser(User user);

    User updateUser(Long id, User user);

    User getUserById(Long id);

    List<User> getUsers();

    void deleteUser(Long id);

    boolean validateUniqueEmail(String email);
}
