package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa addMpa(Mpa mpa);

    Mpa updateMpa(Mpa mpa);

    Mpa deleteMpa(Integer id);

    Mpa getMpa(Integer id);

    List<Mpa> getAllMpa();


}
