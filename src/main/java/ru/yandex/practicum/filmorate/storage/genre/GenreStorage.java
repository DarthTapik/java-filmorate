package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre addGenre(Genre genre);

    Genre updateGenre(Genre genre);

    Genre deleteGenre(Integer id);

    Genre getGenre(Integer id);

    List<Genre> getGenres();


}
