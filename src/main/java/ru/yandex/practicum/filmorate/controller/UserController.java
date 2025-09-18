package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание пользователя");
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            String errorMessage = "Имейл должен быть указан";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        Matcher matcher = Pattern
                .compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                .matcher(user.getEmail());

        if (!matcher.matches()) {
            String errorMessage = "Некорректный имейл";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            String errorMessage = "Этот имейл уже используется";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String errorMessage = "Логин не может содержать пробелы";
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

        if (user.getBirthday().isAfter(LocalDate.now())) {
            String errorMessage = "Дата рождения не может быть в будущем";
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
    public User update(@RequestBody User updateUser) {
        log.info("Обновление данных пользователя");
        Long id = updateUser.getId();

        if (id == null) {
            String errorMessage = "Id должен быть указан";
            log.error("Ошибка валидации обновления данных пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (users.containsKey(id)) {
            User oldUser = users.get(id);

            String newEmail = updateUser.getEmail();
            if (newEmail != null && !newEmail.isBlank() && !oldUser.getEmail().equals(newEmail)) {
                Matcher matcher = Pattern
                        .compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                        .matcher(newEmail);

                if (!matcher.matches()) {
                    String errorMessage = "Некорректный имейл";
                    log.error("Ошибка валидации обновления имейла пользователя: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }

                if (users.values().stream()
                        .map(User::getEmail)
                        .anyMatch(email -> email.equals(newEmail))) {
                    String errorMessage = "Этот имейл уже используется";
                    log.error("Ошибка валидации обновления имейла пользователя: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }

                oldUser.setEmail(newEmail);

            }

            String newLogin = updateUser.getLogin();
            if (newLogin != null && !newLogin.isBlank() && !oldUser.getLogin().equals(newLogin)) {
                if (newLogin.contains(" ")) {
                    String errorMessage = "Логин не может содержать пробелы";
                    log.error("Ошибка валидации обновления логина: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }

                if (users.values().stream()
                        .map(User::getLogin)
                        .anyMatch(login -> login.equals(newLogin))) {
                    String errorMessage = "Логин: " + newLogin + " уже занят";
                    log.error("Ошибка валидации обновления логина: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                oldUser.setLogin(newLogin);

            }

            String newName = updateUser.getName();
            if (newName != null && !newName.isBlank() && !newName.equals(oldUser.getName())) {
                oldUser.setName(newName);
            }

            LocalDate newBirthday = updateUser.getBirthday();
            if (newBirthday != null && !newBirthday.equals(oldUser.getBirthday())) {
                if (newBirthday.isAfter(LocalDate.now())) {
                    String errorMessage = "Дата рождения не может быть в будущем";
                    log.error("Ошибка валидации обновления дня рождения: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                oldUser.setBirthday(newBirthday);
            }

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
