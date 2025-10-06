package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import java.util.Comparator;
import java.util.List;


@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        if (!isDateReleaseValidate(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза не может быть до 28-12-1895");
        }
        filmStorage.create(film);
        return film;
    }

    public Film update(Film updatedFilm) {
        Long id = updatedFilm.getId();

        if (id == null) {
            String errorMessage = "Id должен быть указан";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        Film oldFilm = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));

        LocalDate newReleaseDate = updatedFilm.getReleaseDate();
        if (newReleaseDate != null) {
            if (!isDateReleaseValidate(newReleaseDate)) {
                throw new ValidationException("Дата релиза не может быть до 28-12-1895");
            }
            oldFilm.setReleaseDate(newReleaseDate);
        }

        oldFilm.setName(updatedFilm.getName());
        oldFilm.setDescription(updatedFilm.getDescription());
        oldFilm.setDuration(updatedFilm.getDuration());

        return oldFilm;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        film.getLikes().add(userId);
    }

    public void deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    public boolean isDateReleaseValidate(LocalDate releaseDate) {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 27));
    }

}
