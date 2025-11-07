package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Genre {
    private int id;
    private String name;

    public static HashSet<Genre> parseGenresFromString(String genres) {
        log.info("parseGenresFromString has started");
        log.info("Строка genres до парсинга -> {}", genres);
        List<Genre> genreList = new ArrayList<>();

        if (genres == null || genres.isEmpty()) {
            log.info("() -> Фильм без жанров");
            return new HashSet<Genre>(genreList);
        }

        String[] genrePairs = genres.split(",");
        for (String pair : genrePairs) {

            if (pair == null || pair.trim().isEmpty()) {
                continue;
            }

            String[] parts = pair.split(":");

            if (parts.length >= 2) {
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();

                    Genre genre = new Genre(id, name);
                    genreList.add(genre);
                } catch (NumberFormatException e) {
                    log.info("Ошибка парсинга ID: '" + parts[0] + "' не является числом");
                } catch (Exception e) {
                    log.info("Ошибка при парсинге жанров");
                }
            }
        }

        log.info("genreList before sorting -> {}", genreList);
        genreList.sort(Comparator.comparing(Genre::getId));
        log.info("genreList after sorting -> {}", genreList);
        HashSet<Genre> genresSet = new HashSet<>(genreList);
        log.info("parseGenresFromString has finished");
        log.info("Список жанров фильма после парсинга: {}", genresSet);
        return genresSet;
    }

}
