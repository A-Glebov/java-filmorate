package ru.yandex.practicum.filmorate.mappers.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("GenreRowMapper has stated");
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("name"));
        log.info("GenreRowMapper has finished");
        log.info("genre -> {}", genre);
        return genre;
    }
}