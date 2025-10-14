package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        Long id = getNextId();
        film.setId(id);
        log.trace("Фильму присвоен id {}", id);
        films.put(id, film);
        log.info("Фильм id {} добавлен в хранилище", id);
        return film;
    }

    @Override
    public Film save(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм id {} обновлен в хранилище", film.getId());
        return film;
    }

    @Override
    public Optional<Film> findById(long filmId) {
        log.info("Запрос на получение фильма по id из хранилища");
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public List<Film> findAll() {
        log.info("Запрос на получение всех фильмов из хранилища");
        return new ArrayList<>(films.values());
    }

    private long getNextId() {
        log.info("Генерация id фильма");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("id фильма сгенерирован");
        return ++currentMaxId;
    }

}
