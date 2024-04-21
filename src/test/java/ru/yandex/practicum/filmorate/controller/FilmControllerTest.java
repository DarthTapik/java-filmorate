package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class FilmControllerTest {

    FilmController filmController;
    Film film;

    @BeforeEach
    void BeforeEach(){
        filmController = new FilmController();
        film = new Film(0,"Звездные войны", "Фильм о событиях далекой галактики",
                LocalDate.of(1977, Month.MAY, 25), 121);
    }

    @Test
    void createFilm(){
        Film createdFilm = filmController.create(film);
        ArrayList<Film> films = new ArrayList<>(filmController.findAll());
        Assertions.assertFalse(films.isEmpty());
        Assertions.assertEquals(createdFilm, films.get(0));
    }

    @Test
    void updateFilm(){
        Film createdFilm = filmController.create(film);
        createdFilm.setName("Не звездные войны");
        filmController.update(createdFilm);
        ArrayList<Film> films = new ArrayList<>(filmController.findAll());
        Assertions.assertEquals(createdFilm, films.get(0));
    }

}
