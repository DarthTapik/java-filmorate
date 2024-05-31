package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserOperationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public Collection<User> getAllUser() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public User createUser(User user) {
        user.setFriends(new HashSet<>());
        log.debug("Создан пользователь с id " + user.getId());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.debug("Обновлен пользователь с id " + user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriendToUser(int userId, int friendId) {
        if (userId == friendId) {
            throw new UserOperationException(
                    "Пользователь не может добавить самого себя в друзья");
        }
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.addFriend(friend.getId());
        userStorage.updateUser(user);
        log.info("Пользователь " + userId + " добавил в друзья " + friendId);
    }

    public void removeFriendFromUser(int userId, int friendId) {
        if (userId == friendId) {
            throw new RuntimeException("Пользователь не может удалить самого себя из друзей");
        }
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.removeFriend(friend.getId());
        log.debug("Пользователь " + userId + " убрал из друзей " + friendId);
        userStorage.updateUser(user);
    }

    public Collection<User> getUsersFriends(int userId) {
        User user = userStorage.getUser(userId);
        Set<Integer> friendsId = user.getFriends();
        List userList = friendsId.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
        return new ArrayList<User>(userList);
    }

    public Collection<User> getUsersCommonFriends(int userId, int friendId) {
        User friend = userStorage.getUser(friendId);
        Set<Integer> friendFriendsId = friend.getFriends();

        User user = userStorage.getUser(userId);
        Set<Integer> userFriendsId = user.getFriends();

        List commonFriends = friendFriendsId.stream()
                .filter(userFriendsId::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
        return new ArrayList<>(commonFriends);
    }
}
