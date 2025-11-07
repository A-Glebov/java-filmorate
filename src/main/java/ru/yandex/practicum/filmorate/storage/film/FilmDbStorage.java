package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.mappers.user.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Film create(Film film) {
        log.info("Запрос в хранилище на создание фильма");
        String query = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(query, new String[]{"film_id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setLong(4, film.getDuration());
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        log.info("Сгенерирован id {} для фильма с названием -> {}", id, film.getName());
        film.setId(id);
        log.info("Фильм успешно сохранен -> {}", film.getName());
        return film;
    }

    @Override
    public Film save(Film film) {
        log.info("Запрос в хранилище на обновление фильма");
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
        log.debug("query: {}", query);
        log.debug("new film: {}", film);
        jdbcTemplate.update(query, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        return film;
    }

    @Override
    public Optional<Film> findById(long filmId) {
        log.info("Запрос в хранилище на поиск фильма filmId: {}", filmId);
        String query = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.rating_id, m.name AS rating, " +  // ← добавлено description
                "GROUP_CONCAT(g.genre_id || ':' || g.name SEPARATOR ',') AS genres " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id = ? " +
                "GROUP BY f.film_id";

        try {
            List<Film> results = jdbcTemplate.query(query, filmRowMapper, filmId);

            if (results.isEmpty()) {
                log.info("Фильм с id {} не найден.", filmId);
                return Optional.empty();
            } else {
                log.info("Фильм с id {} успешно найден.", filmId);
                return Optional.of(results.getFirst());
            }
        } catch (Exception e) {
            log.info("ОШИБКА СОХРАНЕНИЯ В ХРАНИЛИЩЕ");
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        log.info("Запрос в хранилище на получение всех фильмов");
        String query = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.rating_id, m.name AS rating, " +  // ← добавлено description
                "GROUP_CONCAT(g.genre_id || ':' || g.name SEPARATOR ',') AS genres " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "GROUP BY f.film_id";

        return jdbcTemplate.query(query, filmRowMapper);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрос на получение популярных фильмов");
        String query = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,\s
                       f.rating_id, m.name as rating,\s
                       COUNT(l.film_id) as likes_count,\s
                       GROUP_CONCAT(g.genre_id || ':' || g.name SEPARATOR ',') AS genres\s
                FROM films f\s
                LEFT JOIN mpa m ON f.rating_id = m.rating_id\s
                LEFT JOIN likes l ON f.film_id = l.film_id\s
                LEFT JOIN film_genres fg ON f.film_id = fg.film_id\s
                LEFT JOIN genres g ON fg.genre_id = g.genre_id\s
                GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration,\s
                         f.rating_id, m.name\s
                ORDER BY likes_count DESC\s
                LIMIT ?\s
                """;
        return jdbcTemplate.query(query, filmRowMapper, count);
    }

}
