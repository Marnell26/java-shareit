package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long id = 1;

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail().toLowerCase());
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        users.put(id, user);
        emails.add(user.getEmail().toLowerCase());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public boolean validateUniqueEmail(String email) {
        return emails.contains(email);
    }

    private Long generateId() {
        return id++;
    }

}
