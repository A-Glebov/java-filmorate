package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;


import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Запрос на получение списка все фильмов");
        List<Film> allFilms = filmService.findAll();
        log.info("Список всех фильмов успешно получен");
        return allFilms;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на создание фильма");
        Film createdFilm = filmService.create(film);
        log.info("Фильм успешно создан");
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        log.info("Запрос на обновление фильма id: {}", updatedFilm.getId());
        Film film = filmService.update(updatedFilm);
        log.info("Фильм id: {} успешно  обновлен", updatedFilm.getId());
        return film;
    }

    @PutMapping("{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId,
                        @PathVariable long userId) {
        log.info("Пользователь id: {} ставит лайк фильму id: {}", userId, filmId);
        filmService.addLike(filmId, userId);
        log.info("Лайк поставлен");
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId,
                           @PathVariable long userId) {
        log.info("Запрос на удаление лайка");
        filmService.deleteLike(filmId, userId);
        log.info("Лайк успешно удален");
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
        log.info("Получен запрос на получение {} популярных фильмов", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Список {} популярных фильмов успешно получен", popularFilms.size());
        return popularFilms;
    }

}
