package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private static final String FILM_INSERT_QUERY =
            "INSERT INTO Film (name, description, releaseDate, duration, likesCount, mpa) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String FILM_UPDATE_QUERY = "UPDATE Film SET " +
            "name = ?, description = ?, releaseDate = ?, duration = ?, likesCount = ?, mpa = ? " +
            "WHERE film_id = ?";
    private static final String FILM_DELETE_QUERY = "DELETE FROM Film " +
            "WHERE film_id = ?";

    private static final String FILM_GET_QUERY = "SELECT f.* FROM Film as f " +
            "WHERE film_id = ?";
    private static final String FILM_GET_LIKES_QUERY = "SELECT l.user_id " +
            "FROM Film f " +
            "JOIN Likes l ON f.film_id = l.film_id " +
            "WHERE f.film_id = ?";
    private static final String FILM_GET_GENRE_QUERY = "SELECT fg.genre_id " +
            "FROM Film f " +
            "JOIN Film_Genre fg ON f.film_id = fg.film_id " +
            "WHERE f.film_id = ?";

    private static final String FILM_DELETE_GENRE_QUERY = "DELETE FROM Film_Genre " +
            "WHERE film_id = ? ";
    private static final String FILM_DELETE_LIKE_QUERY = "DELETE FROM Likes " +
            "WHERE film_id = ? AND user_id = ? ";
    private static final String FILM_INSERT_GENRE_QUERY = "INSERT INTO " +
            "Film_Genre(film_id, genre_id) " +
            "VALUES(?, ?)";
    private static final String FILM_INSERT_LIKE_QUERY = "INSERT INTO " +
            "Likes(film_id, user_id) " +
            "VALUES(?, ?)";
    private static final String FILM_GET_ALL_QUERY = "SELECT * FROM Film";


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(FILM_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getLikesCount());
            ps.setInt(6, film.getMpa().getId());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().intValue());
        }

        int id = film.getId();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(FILM_INSERT_GENRE_QUERY, id, genre.getId());
            }
        }

        if (!film.getLikes().isEmpty()) {
            for (int userId : film.getLikes()) {
                jdbcTemplate.update(FILM_INSERT_LIKE_QUERY, id, userId);
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilm(film.getId()) == null) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        int rowsAffected = jdbcTemplate.update(FILM_UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getLikesCount(),
                film.getMpa().getId(),
                film.getId());
        jdbcTemplate.update(FILM_DELETE_GENRE_QUERY, film.getId()); // т.к. жанров не так много
        int id = film.getId();
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(FILM_INSERT_GENRE_QUERY, id, genre.getId());
            }
        }

        Set<Integer> oldLikes = getLikes(film.getId());
        Set<Integer> removeLikes = oldLikes;
        removeLikes.removeAll(film.getLikes());
        if (!removeLikes.isEmpty()) {
            for (int userId : removeLikes) {
                jdbcTemplate.update(FILM_DELETE_LIKE_QUERY, id, userId);
            }
        }
        oldLikes = getLikes(film.getId());
        Set<Integer> addLikes = film.getLikes();
        addLikes.removeAll(oldLikes);

        if (!addLikes.isEmpty()) {
            for (Integer userId : addLikes) {
                jdbcTemplate.update(FILM_INSERT_LIKE_QUERY, id, userId);
            }
        }

        return film;
    }

    @Override
    public Film deleteFilm(Integer id) {
        Film film = getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        jdbcTemplate.update(FILM_DELETE_QUERY, id);
        jdbcTemplate.update(FILM_DELETE_GENRE_QUERY, id);
        for (int likeId : film.getLikes()) {
            jdbcTemplate.update(FILM_DELETE_LIKE_QUERY, id, likeId);
        }
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject(FILM_GET_QUERY, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query(FILM_GET_ALL_QUERY, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("releaseDate").toLocalDate());
        film.setLikesCount(resultSet.getInt("likesCount"));
        film.setDuration(resultSet.getInt("duration"));
        film.setLikes(getLikes(film.getId()));
        film.setGenres(getGenres(film.getId()));
        film.setMpa(mpaStorage.getMpa(resultSet.getInt("mpa")));
        return film;
    }

    private Set<Integer> getLikes(int id) {
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(FILM_GET_LIKES_QUERY,
                (rs, rowNum) -> rs.getInt("user_id"), id));
        return likes;
    }

    private Set<Genre> getGenres(int id) {
        Set<Integer> genresId = new HashSet<>(jdbcTemplate.query(FILM_GET_GENRE_QUERY,
                (rs, rowNum) -> rs.getInt("genre_id"), id));
        Set<Genre> genres = new HashSet<>(genresId.stream()
                .map(value -> genreStorage.getGenre(value))
                .collect(Collectors.toSet()));

        return genres;
    }

}
