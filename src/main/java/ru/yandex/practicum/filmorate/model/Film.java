package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
/**
 * Film.
 */

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
}
