package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Long id;

    @Email
    private String email;

    @NotBlank(message = "Логин не может быть null или пустым")
    @Pattern(regexp = "^\\S+$")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

}
