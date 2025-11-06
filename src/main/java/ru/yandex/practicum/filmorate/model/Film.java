package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;

    @NotBlank
    private String name;

    @Length(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Long duration;

    @NotNull
    private Mpa mpa;

    private HashSet<Genre> genres = new HashSet<>();
    //private final LinkedHashSet<Genre> genres = new LinkedHashSet<>();


}
