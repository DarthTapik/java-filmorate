package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getFilms());
    }

    public Film createFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        Film oldFilm = filmStorage.getFilm(film.getId());
        film.setLikes(oldFilm.getLikes());
        film.setLikesCount(oldFilm.getLikesCount());
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public void addLikeToFilm(int filmId, int userId) {
        User user = userService.getUser(userId); // Если пользователь не найден, вызывается исключение
        filmStorage.getFilm(filmId).addLike(user.getId());
    }

    public void removeLikeFromFilm(int filmId, int userId) {
        User user = userService.getUser(userId);
        filmStorage.getFilm(filmId).deleteLike(user.getId());
    }

    public Collection<Film> getMostLikedFilms(int count) {
        List<Film> films = filmStorage.getFilms();
        if (count > films.size()) {
            count = films.size();
        }
        return films.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
