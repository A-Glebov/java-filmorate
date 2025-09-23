package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя");

        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            String errorMessage = "Этот имейл уже используется";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (users.values().stream()
                .map(User::getLogin)
                .anyMatch(login -> login.equals(user.getLogin()))) {
            String errorMessage = "Логин: " + user.getLogin() + " уже занят";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан");

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        log.info("Обновление данных пользователя");
        Long id = updatedUser.getId();

        if (id == null) {
            String errorMessage = "Id должен быть указан";
            log.error("Ошибка валидации обновления данных пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (users.containsKey(id)) {
            User oldUser = users.get(id);

            String newEmail = updatedUser.getEmail();
            if (!newEmail.equals(oldUser.getEmail())) {
                if (users.values().stream()
                        .map(User::getEmail)
                        .anyMatch(email -> email.equals(newEmail))) {
                    String errorMessage = "Этот имейл уже используется";
                    log.error("Ошибка валидации обновления имейла пользователя: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                oldUser.setEmail(newEmail);
            }

            String newLogin = updatedUser.getLogin();
            if (!oldUser.getLogin().equals(newLogin)) {
                if (users.values().stream()
                        .map(User::getLogin)
                        .anyMatch(login -> login.equals(newLogin))) {
                    String errorMessage = "Логин: " + newLogin + " уже занят";
                    log.error("Ошибка валидации обновления логина: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                oldUser.setLogin(newLogin);

            }

            String newName = updatedUser.getName();
            if (newName == null || newName.isBlank()) {
                oldUser.setName(newLogin);
            } else {
                oldUser.setName(newName);
            }

            oldUser.setBirthday(updatedUser.getBirthday());
            log.info("Данные пользователя обновлены");

            return oldUser;
        }

        String errorMessage = "Пользователь id = " + id + " не найден";
        log.error("Ошибка валидации обновления данных пользователя: {}", errorMessage);
        throw new NotFoundException(errorMessage);

    }



    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
