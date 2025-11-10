package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.film.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenreInfo;
import ru.yandex.practicum.filmorate.model.FilmGenres;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

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

    public void setGenresToFilms(List<Film> films) {
        String sql = "SELECT fg.film_id, fg.genre_id, g.name AS genre_name " +
                "FROM film_genres fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "ORDER BY fg.film_id ASC";

        List<FilmGenreInfo> filmGenresInfo = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FilmGenreInfo info = new FilmGenreInfo();
            info.setFilmId(rs.getLong("film_id"));

            FilmGenres filmGenres = new FilmGenres();
            filmGenres.setFilmId(rs.getLong("film_id"));
            filmGenres.setGenreId(rs.getObject("genre_id", Integer.class));
            info.setFilmGenres(filmGenres);

            if (rs.getObject("genre_id") != null) {
                Genre genre = new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre_name")
                );
                info.setGenre(genre);
            }

            return info;
        });

        Map<Long, List<Genre>> filmGenresMap = filmGenresInfo.stream()
                .filter(info -> info.getGenre() != null)
                .collect(Collectors.groupingBy(
                        FilmGenreInfo::getFilmId,
                        Collectors.mapping(FilmGenreInfo::getGenre, Collectors.toList())
                ));

        films.forEach(film -> {
            List<Genre> genres = filmGenresMap.get(film.getId());
            if (genres != null) {
                film.getGenres().addAll(genres);
            }
        });
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



