package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    UserController userController;
    User user;

    @BeforeEach
    void beforeEach() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        user = new User(0, "someemail@yandex.ru", "somelogin", "Олег",
                LocalDate.of(2004, Month.MAY, 18), new HashSet<>());
    }

    @Test
    void createUser() {
        User createdUser = userController.create(user);
        ArrayList<User> users = new ArrayList<>(userController.findAll());
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertEquals(createdUser, users.get(0));
    }

    @Test
    void updateFilm() {
        User createdUser = userController.create(user);
        createdUser.setName("Игорь");
        userController.update(user);
        ArrayList<User> users = new ArrayList<>(userController.findAll());
        Assertions.assertEquals(createdUser, users.get(0));
    }

}
