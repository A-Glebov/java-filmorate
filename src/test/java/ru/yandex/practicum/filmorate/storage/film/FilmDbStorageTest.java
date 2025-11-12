package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmDbStorage.class,
        FilmRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    @Autowired
    private final FilmDbStorage filmDbStorage;

    private Film testFilm;
    HashSet<Genre> genres = new HashSet<>();

    @BeforeEach
    public void setUp() {
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));

        testFilm = new Film();
        testFilm.setName("TestFilm");
        testFilm.setDescription("TestDescription");
        testFilm.setReleaseDate(LocalDate.of(1990, 1, 1));
        testFilm.setDuration(120L);
        testFilm.setMpa(new Mpa(1, "G"));
        testFilm.setGenres(genres);

    }

    @Test
    public void testCreateFilm() {
        Film createdFilm = filmDbStorage.create(testFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo(testFilm.getName());
        assertThat(createdFilm.getReleaseDate()).isEqualTo(testFilm.getReleaseDate());
        assertThat(createdFilm.getDuration()).isEqualTo(testFilm.getDuration());
        assertThat(createdFilm.getMpa()).isEqualTo(testFilm.getMpa());
        assertThat(createdFilm.getGenres().size()).isEqualTo(testFilm.getGenres().size());

    }

    @Test
    public void testUpdateFilm() {
        Film updatedFilm = new Film();
        updatedFilm.setName("UpdatedFilm");
        updatedFilm.setDescription("UpDescription");
        updatedFilm.setReleaseDate(LocalDate.of(1991, 1, 2));
        updatedFilm.setDuration(100L);
        updatedFilm.setMpa(new Mpa(2, "PG"));
        genres.add(new Genre(3, "PG-13"));
        updatedFilm.setGenres(genres);

        Film savedFilm = filmDbStorage.save(updatedFilm);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getName()).isEqualTo(updatedFilm.getName());
        assertThat(savedFilm.getDescription()).isEqualTo(updatedFilm.getDescription());
        assertThat(savedFilm.getReleaseDate()).isEqualTo(updatedFilm.getReleaseDate());
        assertThat(savedFilm.getDuration()).isEqualTo(updatedFilm.getDuration());
        assertThat(savedFilm.getMpa()).isEqualTo(updatedFilm.getMpa());
        assertThat(savedFilm.getGenres().size()).isEqualTo(updatedFilm.getGenres().size());

    }

    @Test
    public void testUpdatedNoSuchFilm() {
        Film updatedFilm = new Film();
        updatedFilm.setId(999L);
        updatedFilm.setName("UpdatedFilm");
        updatedFilm.setDescription("UpDescription");
        updatedFilm.setReleaseDate(LocalDate.of(1991, 1, 2));
        updatedFilm.setDuration(100L);
        updatedFilm.setMpa(new Mpa(2, "PG"));
        genres.add(new Genre(3, "PG-13"));
        updatedFilm.setGenres(genres);

        filmDbStorage.save(updatedFilm);
        Optional<Film> savedFilm = filmDbStorage.findById(999);

        assertThat(savedFilm).isEmpty();

    }

    @Test
    public void testFindFilmById() {
        Film savedFilm = filmDbStorage.create(testFilm);
        Film findFilm = filmDbStorage.findById(savedFilm.getId()).get();

        assertThat(findFilm).isNotNull();
        assertThat(findFilm.getName()).isEqualTo(testFilm.getName());
        assertThat(findFilm.getDescription()).isEqualTo(testFilm.getDescription());
        assertThat(findFilm.getReleaseDate()).isEqualTo(testFilm.getReleaseDate());
        assertThat(findFilm.getDuration()).isEqualTo(testFilm.getDuration());
        assertThat(findFilm.getMpa()).isEqualTo(testFilm.getMpa());

    }

    @Test
    public void testNotFindFilmById() {
        Optional<Film> optFilm = filmDbStorage.findById(999);

        assertThat(optFilm).isEmpty();

    }

    @Test
    public void testFindAll() {
        Film updatedFilm = new Film();
        updatedFilm.setName("UpdatedFilm");
        updatedFilm.setDescription("UpDescription");
        updatedFilm.setReleaseDate(LocalDate.of(1991, 1, 2));
        updatedFilm.setDuration(100L);
        updatedFilm.setMpa(new Mpa(2, "PG"));
        genres.add(new Genre(3, "PG-13"));
        updatedFilm.setGenres(genres);

        filmDbStorage.create(testFilm);
        filmDbStorage.create(updatedFilm);

        List<Film> foundFilms = filmDbStorage.findAll();

        assertThat(foundFilms).isNotNull();
        assertThat(foundFilms.size()).isEqualTo(2);

    }

}