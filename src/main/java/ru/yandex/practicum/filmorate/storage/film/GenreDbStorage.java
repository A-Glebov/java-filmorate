package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.film.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper = new GenreRowMapper();

    public List<Genre> findAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    public Optional<Genre> findGenreById(int id) {
        String query = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> results = jdbcTemplate.query(query, genreRowMapper, id);
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.getFirst());
        }

    }

    public HashSet<Genre> findAllGenresByFilm(Film film) {
        log.info("Работает GenreDbStorage.findAllGenresByFilm");

        String sql = "SELECT g.* FROM GENRES g " +
                "JOIN film_genres fg ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";

        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, film.getId());
        return new HashSet<>(genres);
    }

    public List<Genre> findGenresByIds(Collection<Integer> genreIds) {
        String query = "SELECT * FROM genres WHERE genre_id IN (%s)";
        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        String sqlFormatted = String.format(query, inSql);
        return jdbcTemplate.query(
                sqlFormatted,
                genreIds.toArray(),
                genreRowMapper
        );
    }

}



