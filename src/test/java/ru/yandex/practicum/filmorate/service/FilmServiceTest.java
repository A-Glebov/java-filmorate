package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    InMemoryFilmStorage inMemoryFilmStorage;
    FilmService filmService;
    UserStorage userStorage;

    Film film;
    Film updateFilm;

    @BeforeEach
    public void init() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(inMemoryFilmStorage, userStorage);

        film = Film.builder()
                .name("Film1")
                .description("D".repeat(200))
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1L)
                .build();

        updateFilm = Film.builder()
                .id(1L)
                .name("UpdateFilm")
                .description("U".repeat(200))
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1L)
                .build();
    }

    @Test
    public void createWithValidFieldsAndFindAll() {
        filmService.create(film);

        assertEquals(1, film.getId(), "Фильму не присвоен id");
        assertEquals(1, filmService.findAll().size(), "Фильм не добавлен");
    }

    @Test
    public void createFilmWithDateBefore28dec1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class,
                () -> filmService.create(film), "Дата релиза ранее 28.12.1895 должна приводить к исключению");
    }

    @Test
    public void updateFilmWithNonExistentId() {
        filmService.create(film);
        film.setId(2L);

        assertThrows(NotFoundException.class,
                () -> filmService.update(film),
                "Обновление фильма с несуществующим id должно приводить к исключению");
    }

    @Test
    public void updateFileWithoutId() {
        film.setId(null);

        assertThrows(ValidationException.class,
                () -> filmService.update(film), "id не должен равняться null");
    }

    @Test
    public void updateDescription() {
        filmService.create(film);
        filmService.update(updateFilm);

        assertEquals(1, filmService.findAll().size(), "Изменилось количество задач");
        assertEquals(updateFilm.getDescription(), film.getDescription(), "Описание фильма должно измениться");
    }

    @Test
    public void updateDateBefore28Dec1895() {
        updateFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        filmService.create(film);
        assertThrows(ValidationException.class,
                () -> filmService.update(updateFilm),
                "Описание более 200 символов должно приводить к исключению");

    }

    @Test
    public void updateValidDate() {
        updateFilm.setReleaseDate(LocalDate.of(1895, 12, 29));
        filmService.create(film);
        filmService.update(updateFilm);

        assertEquals(updateFilm.getReleaseDate(), film.getReleaseDate(), "Дата релиза должна измениться");
    }

    @Test
    public void updateValidDuration() {
        updateFilm.setDuration(120L);
        filmService.create(film);
        filmService.update(updateFilm);

        assertEquals(updateFilm.getDuration(), film.getDuration(), "Дата релиза должна измениться");
    }

}