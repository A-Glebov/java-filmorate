package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);
    Film save(Film film);
    Optional<Film> findById(long filmId);
    List<Film> findAll();
}
