package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class UserControllerTest {

    UserController userController;
    User user;

    @BeforeEach
    void BeforeEach(){
        userController = new UserController();
        user = new User(0,"someemail@yandex.ru", "somelogin", "Олег",
                LocalDate.of(2004, Month.MAY, 18));
    }

    @Test
    void createUser(){
        User createdUser = userController.create(user);
        ArrayList<User> users = new ArrayList<>(userController.findAll());
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertEquals(createdUser, users.get(0));
    }

    @Test
    void updateFilm(){
        User createdUser = userController.create(user);
        createdUser.setName("Игорь");
        userController.update(user);
        ArrayList<User> users = new ArrayList<>(userController.findAll());
        Assertions.assertEquals(createdUser, users.get(0));
    }

}
