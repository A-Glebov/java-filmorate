package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

/**
 * Film.
 */
@Slf4j
@Data
@Builder
public class Film {
    private Long id;

    @NotBlank
    private String name;

    @Length(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Long duration;

    public void dateReleaseValidate() {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            String errorMessage = "Дата релиза раньше 28 декабря 1895";
            log.error("Ошибка валидации добавления фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

}
