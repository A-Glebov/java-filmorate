package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.*;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable("filmId") Long filmId) {
        log.info("Запрос на получение списка по id");
        return filmService.findById(filmId);
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Запрос на получение списка все фильмов");
        List<Film> allFilms = filmService.findAll();
        log.info("Список всех фильмов успешно получен");
        return allFilms;
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на создание фильма");
        Film createdFilm = filmService.create(film);
        log.info("Фильм успешно создан");
        return createdFilm;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film updatedFilm) {
        log.info("Запрос на обновление фильма id: {}", updatedFilm.getId());
        Film film = filmService.update(updatedFilm);
        log.info("Фильм id: {} успешно  обновлен", updatedFilm.getId());
        return film;
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId,
                        @PathVariable long userId) {
        log.info("Пользователь id: {} ставит лайк фильму id: {}", userId, filmId);
        filmService.addLike(filmId, userId);
        log.info("Лайк поставлен");
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId,
                           @PathVariable long userId) {
        log.info("Запрос на удаление лайка");
        filmService.deleteLike(filmId, userId);
        log.info("Лайк успешно удален");
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
        log.info("Получен запрос на получение {} популярных фильмов", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Список {} популярных фильмов успешно получен", popularFilms.size());
        return popularFilms;
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpas() {
        log.info("Запрос на получение списка рейтингов");
        return filmService.findAllMpa();
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa findMpaById(@PathVariable int mpaId) {
        log.info("Запрос на получение рейтинга по id");
        return filmService.findMpaById(mpaId);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("жанров");
        return filmService.findAllGenres();
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenresById(@PathVariable int genreId) {
        log.info("Запрос на получение жанра по id");
        return filmService.findGenreById(genreId);
    }

}
