package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer idCounter = 1;


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
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException(
                        "Фильм с id: " + id + " не найден"));
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
