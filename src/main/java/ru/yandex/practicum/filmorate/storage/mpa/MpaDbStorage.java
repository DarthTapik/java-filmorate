package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String MPA_INSERT_QUERY = "INSERT INTO Mpa(name) VALUES (?)";
    private static final String MPA_UPDATE_QUERY = "UPDATE Mpa SET name = ? WHERE mpa_id = ?";
    private static final String MPA_DELETE_QUERY = "DELETE Mpa WHERE mpa_id = ?";
    private static final String MPA_GET_QUERY = "SELECT * FROM Mpa WHERE mpa_id = ?";
    private static final String MPA_GET_ALL_QUERY = "SELECT * FROM Mpa";

    @Override
    public Mpa addMpa(Mpa mpa) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(MPA_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, mpa.getName());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            mpa.setId(keyHolder.getKey().intValue());
        }
        return mpa;
    }

    @Override
    public Mpa updateMpa(Mpa mpa) {
        if (getMpa(mpa.getId()) == null) {
            throw new NotFoundException("Рейтинг с id " + mpa.getId() + " не найден.");
        }
        int rowsAffected = jdbcTemplate.update(MPA_UPDATE_QUERY,
                mpa.getName(),
                mpa.getId());
        return mpa;
    }

    @Override
    public Mpa deleteMpa(Integer id) {
        if (getMpa(id) == null) {
            throw new NotFoundException("Рейтинг с id " + id + " не найден.");
        }
        Mpa mpa = getMpa(id);
        jdbcTemplate.update(MPA_DELETE_QUERY, id);
        return mpa;
    }

    @Override
    public Mpa getMpa(Integer id) {
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(MPA_GET_QUERY, this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Жанр с id " + id + " не найден.");
        }
        return mpa;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(MPA_GET_ALL_QUERY, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
}
