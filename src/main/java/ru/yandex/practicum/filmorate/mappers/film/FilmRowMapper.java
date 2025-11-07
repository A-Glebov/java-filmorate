package ru.yandex.practicum.filmorate.mappers.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("FilmRowMapper has started: {}", resultSet.toString());
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getLong("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("rating_id"));
        mpa.setName(resultSet.getString("rating"));
        film.setMpa(mpa);
        log.info("Обработка списка жанров в FilmRowMapper");
        film.setGenres(Genre.parseGenresFromString(resultSet.getString("genres")));
        log.info("Фильм после обработки  FilmRowMapper -> {}", film);
        log.info("FilmRowMapper finish");
        return film;
    }

}
