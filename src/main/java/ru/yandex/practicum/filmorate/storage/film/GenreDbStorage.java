package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.film.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

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

    public void findAllGenresByFilm(List<Film> films) {
        log.info("Работает GenreDbStorage.findAllGenresByFilm");
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        String sql = "SELECT g.* FROM GENRES g " +
                "JOIN film_genres fg ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (%s)";

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        jdbcTemplate.query(String.format(sql, inSql),
                filmById.keySet().toArray(), genreRowMapper);
    }


}



