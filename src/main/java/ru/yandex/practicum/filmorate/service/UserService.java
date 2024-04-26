package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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
        user.setFriends(new HashSet<>());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        User oldUser = userStorage.getUser(user.getId());
        user.setFriends(oldUser.getFriends());
        return userStorage.updateUser(user);
    }

    public void addFriendToUser(int userId, int friendId) {
        if (userId == friendId) {
            throw new RuntimeException("Пользователь не может добавить самого себя в друзья");
        }
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
    }

    public void removeFriendFromUser(int userId, int friendId) {
        if (userId == friendId) {
            throw new RuntimeException("Пользователь не может удалить самого себя из друзей");
        }
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.removeFriend(friend.getId());
        friend.removeFriend(user.getId());
    }

    public Collection<User> getUsersFriends(int userId) {
        User user = userStorage.getUser(userId);
        Set<Integer> friendsId = user.getFriends();
        return friendsId.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getUsersCommonFriends(int userId, int friendId) {
        User friend = userStorage.getUser(friendId);
        Set<Integer> friendFriendsId = friend.getFriends();

        User user = userStorage.getUser(userId);
        Set<Integer> userFriendsId = user.getFriends();

        return friendFriendsId.stream()
                .filter(userFriendsId::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
