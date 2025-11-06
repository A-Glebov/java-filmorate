package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


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
        // Разделяем строку по запятым
        String[] genrePairs = genres.split(",");
        for (String pair : genrePairs) {

            // Пропускаем пустые элементы
            if (pair == null || pair.trim().isEmpty()) {
                continue;
            }
            // Разделяем каждую пару по двоеточию
            String[] parts = pair.split(":");

            // Проверяем, что есть оба элемента (id и name)
            if (parts.length >= 2) {
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();

                    // Создаем объект Genre и добавляем в список
                    Genre genre = new Genre(id, name);
                    genreList.add(genre);
                } catch (NumberFormatException e) {
                    // Логируем ошибку парсинга числа и продолжаем обработку
                    log.info("Ошибка парсинга ID: '" + parts[0] + "' не является числом");
                } catch (Exception e) {
                    // Логируем другие ошибки
                    log.info("Ошибка при парсинге жанров");
                }
            }
        }

        HashSet<Genre> genresSet = new HashSet<>(genreList);
        log.info("parseGenresFromString has finished");
        log.info("Список жанров фильма после парсинга: {}", genresSet);
        return genresSet;
    }

}
