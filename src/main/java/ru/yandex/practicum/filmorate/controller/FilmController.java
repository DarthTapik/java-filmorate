package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм " + film.getId() + " создан");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм " + film.getId() + " обновлен");
            return film;
        } else {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
    }


    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
