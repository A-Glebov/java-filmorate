package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Добавление фильма");
        if (film.getName() == null || film.getName().isEmpty()) {
            String errorMessage = "Название не может быть пустым";
            log.error("Ошибка валидации добавления фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (film.getDescription().length() > 200) {
            String errorMessage = "Максимальная длина описания - 200 символов";
            log.error("Ошибка валидации добавления фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String errorMessage = "Дата релиза раньше 28 декабря 1895";
            log.error("Ошибка валидации добавления фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (film.getDuration() <= 0) {
            String errorMessage = "Продолжительность не может быть неположительным числом";
            log.error("Ошибка валидации добавления фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен");
        return film;

    }

    @PutMapping
    public Film update(@RequestBody Film updateFilm) {
        log.info("Обновление данных фильма");
        Long id = updateFilm.getId();

        if (id == null) {
            String errorMessage = "Id должен быть указан";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (films.containsKey(id)) {
            Film oldFilm = films.get(id);

            String newName = updateFilm.getName();
            if (newName != null && !newName.isBlank()) {
                oldFilm.setName(newName);
            }

            String newDescription = updateFilm.getDescription();
            if (newDescription != null && !newDescription.isBlank()) {
                if (newDescription.length() > 200) {
                    String errorMessage = "Максимальная длина описания - 200 символов";
                    log.error("Ошибка валидации обновления описания фильма: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                oldFilm.setDescription(newDescription);
            }

            LocalDate newDate = updateFilm.getReleaseDate();
            if (newDate != null) {
                if (newDate.isBefore(LocalDate.of(1895, 12, 28))) {
                    String errorMessage = "Дата релиза раньше 28 декабря 1895";
                    log.error("Ошибка валидации обновления даты релиза фильма: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                oldFilm.setReleaseDate(newDate);
            }

            Long newDuration = updateFilm.getDuration();
            if (newDuration != null) {
                if (updateFilm.getDuration() <= 0) {
                    String errorMessage = "Продолжительность не может быть неположительным числом";
                    log.error("Ошибка валидации обновления продолжительности фильма: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                oldFilm.setDuration(newDuration);
            }

            log.info("Данные фильма успешно обновлены");
            return oldFilm;
        }

        String errorMessage = "Фильм с id = " + id + " не найден";
        log.error(errorMessage);
        throw new NotFoundException(errorMessage);

    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;

    }
}
