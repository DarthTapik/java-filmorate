package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserOperationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final UserService userService;


    public Collection<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getFilms());
    }

    public Film createFilm(Film film) {
        if (film.getMpa().getId() > 5) {
            throw new UserOperationException("Неверный рейтинг");
        }
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() > 6) {
                    throw new UserOperationException("Неверный жанр");
                }
            }
        }
        log.debug("Создан фильм с id " + film.getId());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {

        log.debug("Обновлен фильм с id " + film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public void addLikeToFilm(int filmId, int userId) {
        User user = userService.getUser(userId); // Если пользователь не найден, вызывается исключение
        Film film = filmStorage.getFilm(filmId);
        film.addLike(user.getId());
        log.info("Добавлен лайк на фильм " + filmId + " пользователем " + userId);
        filmStorage.updateFilm(film);
    }

    public void removeLikeFromFilm(int filmId, int userId) {
        User user = userService.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        film.deleteLike(user.getId());
        log.info("Удален лайк с фильма " + filmId + " пользователем " + userId);
        filmStorage.updateFilm(film);
    }

    public Collection<Film> getMostLikedFilms(int count) {
        List<Film> films = filmStorage.getFilms();
        if (count > films.size()) {
            count = films.size();
        }
        List mostLikedFilms = films.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
        return new ArrayList<>(mostLikedFilms);
    }
}
