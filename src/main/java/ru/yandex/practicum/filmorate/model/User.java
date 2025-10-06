package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private Set<Long> friends = new HashSet<>();
}
