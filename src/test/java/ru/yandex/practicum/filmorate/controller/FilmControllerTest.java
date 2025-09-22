package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController = new FilmController();
    Film film;
    Film updateFilm;

    @BeforeEach
    public void init() {
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
        filmController.create(film);

        assertEquals(1, film.getId(), "Фильму не присвоен id");
        assertEquals(1, filmController.findAll().size(), "Фильм не добавлен");
    }

    @Test
    public void createFilmWithDateBefore28dec1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class,
                () -> filmController.create(film), "Дата релиза ранее 28.12.1895 должна приводить к исключению");
    }

    @Test
    public void updateFilmWithNonExistentId() {
        filmController.create(film);
        film.setId(2L);

        assertThrows(NotFoundException.class,
                () -> filmController.update(film),
                "Обновление фильма с несуществующим id должно приводить к исключению");
    }

    @Test
    public void updateFileWithoutId() {
        film.setId(null);

        assertThrows(ValidationException.class,
                () -> filmController.update(film), "id не должен равняться null");
    }

    @Test
    public void updateDescription() {
        filmController.create(film);
        filmController.update(updateFilm);

        assertEquals(1, filmController.findAll().size(), "Изменилось количество задач");
        assertEquals(updateFilm.getDescription(), film.getDescription(), "Описание фильма должно измениться");
    }

    @Test
    public void updateDateBefore28Dec1895() {
        updateFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        filmController.create(film);
        assertThrows(ValidationException.class,
                () -> filmController.update(updateFilm),
                "Описание более 200 символов должно приводить к исключению");

    }

    @Test
    public void updateValidDate() {
        updateFilm.setReleaseDate(LocalDate.of(1895, 12, 29));
        filmController.create(film);
        filmController.update(updateFilm);

        assertEquals(updateFilm.getReleaseDate(), film.getReleaseDate(), "Дата релиза должна измениться");
    }

    @Test
    public void updateValidDuration() {
        updateFilm.setDuration(120L);
        filmController.create(film);
        filmController.update(updateFilm);

        assertEquals(updateFilm.getDuration(), film.getDuration(), "Дата релиза должна измениться");
    }

}