package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer idCounter = 1;

    @Override
    public User addUser(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
            return user;
        } else {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
    }

    @Override
    public User deleteUser(Integer id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            users.remove(id);
            return user;
        } else {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }
    }

    @Override
    public User getUser(Integer id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id: " + id + " не найден"));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
