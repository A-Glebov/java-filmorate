package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.film.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper = new MpaRowMapper();

    public List<Mpa> findAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    public Optional<Mpa> findMpaById(int id) {
        log.info("Ищем MPA с id {} в хранилище", id);
        String query = "SELECT * FROM mpa where rating_id = ?";
        log.info("query: {}", query);
        List<Mpa> results = jdbcTemplate.query(query, mpaRowMapper, id);
        if (results.isEmpty()) {

            log.info("MPA с id {} не найден.", id);
            return Optional.empty();
        } else {
            log.info("MPA с id {} успешно найден.", id);
            return Optional.of(results.getFirst());
        }
    }

}
