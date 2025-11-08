package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmGenresDbStorage filmGenresDbStorage;

    public Film findById(Long filmId) {
        log.info("Запрос в сервис на получение фильма id {} -> ", filmId);
        Film film = filmStorage.findById(filmId).orElseThrow(() ->
                new NotFoundException("фильм с id не найден"));
        log.info("Найден фильм : {}", film.toString());
        return film;
    }

    public List<Film> findAll() {
        log.info("Запрос в сервис на получение всех фильмов");
        List<Film> films = filmStorage.findAll();
        genreDbStorage.findAllGenresByFilm(films);
        log.info("Список фильмов: {}", films);
        return films;
    }

    public Film create(Film film) {
        log.info("Запрос в сервис на создание фильма {}", film);
        log.info("Валидация даты релиза при создании фильма");
        if (!isDateReleaseValidate(film.getReleaseDate())) {
            String errorMessage = "Дата релиза не может быть до 28-12-1895";
            log.info("Ошибка валидации при создании фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        log.info("Валидация рейтинга при создании фильма -> {}", film.getMpa());
        Mpa mpa = mpaDbStorage.findMpaById(film.getMpa().getId()).orElseThrow(() ->
                new NotFoundException("MPA с id не найден"));

        log.info("Валидация списка жанров при создании фильма -> {}", film.getGenres());
        Collection<Integer> genresIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        List<Genre> existGenres = genreDbStorage.findGenresByIds(genresIds);

        if (existGenres.size() != film.getGenres().size()) {
            throw new NotFoundException("Жанр не найден");
        }

        filmStorage.create(film);

        filmGenresDbStorage.create(film.getId(), film.getGenres());
        log.info("Фильм создан -> {}", film);
        return film;
    }

    public Film update(Film updatedFilm) {
        log.info("Запрос в сервис на обновление фильма -> {}", updatedFilm);
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

        log.info("Валидация даты релиза при обновлении фильма");
        LocalDate newReleaseDate = updatedFilm.getReleaseDate();
        if (newReleaseDate != null) {
            if (!isDateReleaseValidate(newReleaseDate)) {
                String errorMessage = "Дата релиза не может быть до 28-12-1895";
                log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
        }

        log.info("Валидация рейтинга при обновлении фильма -> {}", updatedFilm.getMpa());
        Mpa mpa = mpaDbStorage.findMpaById(updatedFilm.getMpa().getId()).orElseThrow(() ->
                new NotFoundException("MPA с id не найден"));
        log.info("Фильм обновлен -> {}", updatedFilm);

        //Сохранение в таблицу film_genres
        filmGenresDbStorage.save(updatedFilm.getId(), updatedFilm.getGenres());
        log.info("Фильм успешно обновлен -> {}", updatedFilm);
        return filmStorage.save(updatedFilm);
    }

    public void addLike(long filmId, long userId) {
        log.info("Запрос на добавление лайка фильму");
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        User user = userService.findById(userId);

        likeDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        log.info("Запрос в сервис на удаление лайка фильму");
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        User user = userService.findById(userId);

        likeDbStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрос в сервис на получение списка популярных фильмов");
        List<Film> popularFilms = filmStorage.getPopularFilms(count);
        log.trace("Список {} самых популярных фильмов: {}", count, popularFilms);
        return popularFilms;
    }

    public List<Mpa> findAllMpa() {
        log.info("Запрос в сервис на получение списка всех жанров");
        List<Mpa> mpas = mpaDbStorage.findAllMpa();
        log.trace("Список рейтингов -> {}", mpas);
        return mpas;
    }

    public Mpa findMpaById(int id) {
        log.info("Запрос в сервис на получение рейтинга по id -> {} ", id);
        Mpa mpa = mpaDbStorage.findMpaById(id).orElseThrow(() -> new NotFoundException("Рейтинг MPA не найден."));
        log.trace("Рейтинг получен -> {}", mpa);
        return mpa;
    }

    public List<Genre> findAllGenres() {
        log.info("Запрос в сервис на получение списка всех жанров");
        List<Genre> genres = genreDbStorage.findAllGenres();
        log.trace("Список всех жанров -> {}", genres);
        return genres;
    }

    public Genre findGenreById(int id) {
        log.info("Запрос в сервис на поиск жанра по id -> {} ", id);
        Genre genre = genreDbStorage.findGenreById(id).orElseThrow(() -> new NotFoundException("Жанр не найден."));
        log.trace("Получен жанр {} с id -> {}", genre, id);
        return genre;
    }

    public boolean isDateReleaseValidate(LocalDate releaseDate) {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 27));
    }

}
