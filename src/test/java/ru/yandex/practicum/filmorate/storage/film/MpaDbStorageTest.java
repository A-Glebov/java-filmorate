package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.film.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({MpaDbStorage.class,
        MpaRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Test
    public void testFindAllMpa() {
        List<Mpa> mpas = mpaDbStorage.findAllMpa();

        assertThat(mpas).isNotNull();
        assertThat(mpas.size()).isEqualTo(5);
    }

    @Test
    public void testFindMpaById() {
        Mpa mpa = mpaDbStorage.findMpaById(1).get();

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
    }
}