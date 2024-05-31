package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String USER_INSERT_QUERY =
            "INSERT INTO Users(email, login, name, birthday)" +
                    "values (?, ?, ?, ?)";

    private static final String USER_UPDATE_QUERY = "UPDATE Users set " +
            "email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String USER_DELETE_QUERY = "DELETE FROM Users " +
            "WHERE user_id = ?";

    private static final String USER_GET_QUERY = "SELECT * FROM Users " +
            "WHERE user_id = ?";
    private static final String USER_GET_ALL_QUERY = "SELECT * FROM Users";
    private static final String USER_GET_FRIENDS_QUERY = "SELECT uf.friend_id " +
            "FROM Users u " +
            "JOIN Users_Friends uf ON u.user_id = uf.user_id " +
            "WHERE u.user_id = ?";
    private static final String USER_INSERT_FRIEND_QUERY =
            "INSERT INTO Users_Friends(user_id, friend_id) " +
                    "values(?, ?)";
    private static final String USER_DELETE_FRIEND_QUERY =
            "DELETE Users_Friends " +
                    "WHERE user_id = ? AND friend_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(USER_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().intValue());
            for (Integer id : user.getFriends()) {
                jdbcTemplate.update(USER_INSERT_FRIEND_QUERY, user.getId(), id);
            }
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (getUser(user.getId()) == null) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        int rowsAffected = jdbcTemplate.update(USER_UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        Set<Integer> oldFriends = getFriends(user.getId());

        Set<Integer> removeFriend = oldFriends;
        removeFriend.removeAll(user.getFriends());
        if (!removeFriend.isEmpty()) {
            for (Integer id : removeFriend) {
                jdbcTemplate.update(USER_DELETE_FRIEND_QUERY, user.getId(), id);
            }
        }
        oldFriends = getFriends(user.getId());
        Set<Integer> addFriend = user.getFriends();
        addFriend.removeAll(oldFriends);

        if (!addFriend.isEmpty()) {
            for (Integer id : addFriend) {
                jdbcTemplate.update(USER_INSERT_FRIEND_QUERY, user.getId(), id);
            }
        }

        return user;
    }

    @Override
    public User deleteUser(Integer id) {
        User user = getUser(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        for (int friendId : user.getFriends()) {
            jdbcTemplate.update(USER_GET_FRIENDS_QUERY, id, friendId);
        }
        jdbcTemplate.update(USER_DELETE_QUERY, id);
        return user;

    }

    @Override
    public User getUser(Integer id) {
        User user;

        try {
            user = jdbcTemplate.queryForObject(USER_GET_QUERY, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(USER_GET_ALL_QUERY, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        user.setLogin(resultSet.getString("login"));
        user.setFriends(getFriends(user.getId()));
        return user;
    }

    private Set<Integer> getFriends(Integer id) {
        Set<Integer> friends = new HashSet<>(jdbcTemplate.query(USER_GET_FRIENDS_QUERY,
                (rs, rowNum) -> rs.getInt("friend_id"), id));
        return friends;
    }

}
