package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    private Set<Long> likes = new HashSet<>();

}
