package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.*;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String GENRE_INSERT_QUERY = "INSERT INTO Genre(name) VALUES (?)";
    private static final String GENRE_UPDATE_QUERY = "UPDATE Genre SET name = ? WHERE genre_id = ?";
    private static final String GENRE_DELETE_QUERY = "DELETE Genre WHERE genre_id = ?";
    private static final String GENRE_GET_QUERY = "SELECT * FROM Genre WHERE genre_id = ?";
    private static final String GENRE_GET_ALL_QUERY = "SELECT * FROM Genre";


    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre addGenre(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(GENRE_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, genre.getName());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            genre.setId(keyHolder.getKey().intValue());
        }
        return genre;
    }

    @Override
    public Genre updateGenre(Genre genre) {
        if (getGenre(genre.getId()) == null) {
            throw new NotFoundException("Жанр с id " + genre.getId() + " не найден.");
        }
        int rowsAffected = jdbcTemplate.update(GENRE_UPDATE_QUERY,
                genre.getName(),
                genre.getId());
        return genre;
    }

    @Override
    public Genre deleteGenre(Integer id) {
        if (getGenre(id) == null) {
            throw new NotFoundException("Жанр с id " + id + " не найден.");
        }
        Genre genre = getGenre(id);
        jdbcTemplate.update(GENRE_DELETE_QUERY, id);
        return genre;
    }

    @Override
    public Genre getGenre(Integer id) {
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(GENRE_GET_QUERY, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Жанр с id " + id + " не найден.");
        }
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query(GENRE_GET_ALL_QUERY, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    }
}
