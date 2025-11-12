package ru.yandex.practicum.filmorate.mappers.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenres;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class FilmGenresRowMapper implements RowMapper<FilmGenres> {
    @Override
    public FilmGenres mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("FilmGenresRowMapper started");
        FilmGenres filmGenres = FilmGenres.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getInt("genre_id"))
                .build();
        log.info("FilmGenresRowMapper finished");
        log.info("filmGenres -> {}", filmGenres);
        return filmGenres;
    }
}
