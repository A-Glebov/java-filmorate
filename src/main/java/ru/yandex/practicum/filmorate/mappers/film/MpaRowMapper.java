package ru.yandex.practicum.filmorate.mappers.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class MpaRowMapper implements RowMapper<Mpa> {
    @Override
    public Mpa mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("MpaRowMapper has started");
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("rating_id"));
        mpa.setName(resultSet.getString("name"));
        log.info("MpaRowMapper has finished");
        return mpa;
    }
}
