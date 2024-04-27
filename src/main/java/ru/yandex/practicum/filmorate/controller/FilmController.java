package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.HashSet;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {


    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        film.setLikes(new HashSet<>());
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film get(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unLikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostLikedFilms(count);
    }

}
