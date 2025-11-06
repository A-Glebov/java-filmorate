package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.film.FilmGenresRowMapper;
import ru.yandex.practicum.filmorate.model.FilmGenres;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FilmGenresDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenresRowMapper filmGenresRowMapper;

    public void create(Long filmId, HashSet<Genre> genres) {
        log.info("Запрос на добавление данных в film_genres");
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    public void save(Long filmId, HashSet<Genre> genres) {
        log.info("Запрос на обновление данных в film_genres");
        String sql = "UPDATE users SET film_id = ?, genre_id = ? WHERE film_id = ?";
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    public void delete(FilmGenres filmGenre) {
        log.info("Запрос на удаление данных из талицы film_genres");
        String sql = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";
        jdbcTemplate.update(sql, filmGenre.getFilmId(), filmGenre.getGenreId());
    }

}

