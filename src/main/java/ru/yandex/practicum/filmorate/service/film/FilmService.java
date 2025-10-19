package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        if (!isDateReleaseValidate(film.getReleaseDate())) {
            String errorMessage = "Дата релиза не может быть до 28-12-1895";
            log.info("Ошибка валидации при создании фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        filmStorage.create(film);
        return film;
    }

    public Film update(Film updatedFilm) {
        Long id = updatedFilm.getId();

        if (id == null) {
            String errorMessage = "Id должен быть указан";
            log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        Film oldFilm = filmStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Фильм с id: " + id + " не найден"));
        log.trace("Фильм до обновления: {}", oldFilm);
        LocalDate newReleaseDate = updatedFilm.getReleaseDate();
        if (newReleaseDate != null) {
            if (!isDateReleaseValidate(newReleaseDate)) {
                String errorMessage = "Дата релиза не может быть до 28-12-1895";
                log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            oldFilm.setReleaseDate(newReleaseDate);
            log.trace("Установлена новая дата релиза фильма: {}", oldFilm.getName());
        }

        oldFilm.setName(updatedFilm.getName());
        log.trace("Установлено новое название фильма: {}", oldFilm.getName());
        oldFilm.setDescription(updatedFilm.getDescription());
        log.trace("Установлено новое описание фильма: {}", oldFilm.getDescription());
        oldFilm.setDuration(updatedFilm.getDuration());
        log.trace("Установлена новая продолжительность фильма: {}", oldFilm.getDuration());
        log.trace("Обновленный фильм: {}", oldFilm);
        return oldFilm;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        User user = userService.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id: " + userId + " не найден"));
        log.trace("Список лайков до добавления нового лайка: {}", film.getLikes());
        film.getLikes().add(userId);
        log.trace("Список лайков после добавления нового лайка: {}", film.getLikes());
    }

    public void deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        User user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        log.trace("Список лайков до удаления лайка: {}", film.getLikes());
        film.getLikes().remove(userId);
        log.trace("Список лайков после удаления лайка: {}", film.getLikes());
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
        log.trace("Список {} самых популярных фильмов: {}", count, popularFilms);
        return popularFilms;
    }

    public boolean isDateReleaseValidate(LocalDate releaseDate) {
        log.info("Валидация даты релиза фильма");
        return releaseDate.isAfter(LocalDate.of(1895, 12, 27));
    }

}
