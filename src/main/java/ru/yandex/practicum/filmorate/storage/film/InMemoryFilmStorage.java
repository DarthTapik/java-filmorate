package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;
    private Integer idCounter;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
        idCounter = 1;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(idCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            return film;
        } else {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден");
        }
    }

    @Override
    public Film deleteFilm(Integer id) {
        if (films.containsKey(id)) {
            Film film = films.get(id);
            films.remove(id);
            return film;
        } else {
            throw new NotFoundException("Фильм с id: " + id + " не найден");
        }

    }

    @Override
    public Film getFilm(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм с id: " + id + " не найден");
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
