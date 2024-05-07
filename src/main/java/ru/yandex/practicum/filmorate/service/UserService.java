package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserOperationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public Collection<User> getAllUser() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public User createUser(User user) {
        user.setFriends(new HashMap<>());
        log.debug("Создан пользователь с id " + user.getId());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        User oldUser = userStorage.getUser(user.getId());
        user.setFriends(oldUser.getFriends());
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
        if (friend.getFriends().containsKey(user.getId())) {
            user.addFriend(friend.getId(), FriendshipStatus.CONFIRMED);
            friend.addFriend(user.getId(), FriendshipStatus.CONFIRMED);
            log.debug("Пользователь " + userId
                    + " принял заявку на добавление в друзья " + friendId);
        } else {
            user.addFriend(friend.getId(), FriendshipStatus.NOT_CONFIRMED);
            log.debug("Пользователь " + userId
                    + " подал заявку на добавление в друзья " + friendId);
        }


    }

    public void removeFriendFromUser(int userId, int friendId) {
        if (userId == friendId) {
            throw new RuntimeException("Пользователь не может удалить самого себя из друзей");
        }
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.removeFriend(friend.getId());
        friend.removeFriend(user.getId());
        log.debug("Пользователь " + userId + " убрал из друзей " + friendId);

    }

    public Collection<User> getUsersFriends(int userId) {
        User user = userStorage.getUser(userId);
        HashMap<Integer, FriendshipStatus> friendsId = user.getFriends();
        List<User> userList = friendsId.entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(entry -> userStorage.getUser(entry.getKey()))
                .collect(Collectors.toList());
        return new ArrayList<>(userList);
    }

    public Collection<User> getUsersCommonFriends(int userId, int friendId) {
        User friend = userStorage.getUser(friendId);
        HashMap<Integer, FriendshipStatus> friendFriendsId = friend.getFriends();

        User user = userStorage.getUser(userId);
        HashMap<Integer, FriendshipStatus> userFriendsId = user.getFriends();

        List<User> commonFriends = friendFriendsId.entrySet().stream()
                .filter(entry -> userFriendsId.containsKey(entry.getKey())
                        && userFriendsId.get(entry.getKey()) == FriendshipStatus.CONFIRMED)
                .map(entry -> userStorage.getUser(entry.getKey()))
                .collect(Collectors.toList());

        return new ArrayList<>(commonFriends);
    }
}
