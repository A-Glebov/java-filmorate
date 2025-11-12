package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendDbStorage friendDbStorage;

    public User findById(long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        return user;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        String login = user.getLogin();

        if (isLoginExists(login)) {
            String errorMessage = "Логин: " + user.getLogin() + " уже занят";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(login);
        }

        String email = user.getEmail();
        if (isEmptyEmail(email)) {
            log.error("Ошибка валидации: email должен быть указан");
            throw new ValidationException("email должен быть указан");
        }

        if (isEmailExists(email)) {
            String errorMessage = "Этот email уже используется";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        userStorage.create(user);

        return user;
    }

    public User update(User updatedUser) {
        log.info("Запрос на обновление пользователя id -> {} в сервисе", updatedUser.getId());
        log.info("-> {}", updatedUser);

        Long id = updatedUser.getId();
        if (id == null) {
            String errorMessage = "Id должен быть указан";
            log.error("Ошибка валидации обновления данных пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        User oldUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));

        String newEmail = updatedUser.getEmail();
        log.trace("newEmail -> {}", newEmail);
        if (!newEmail.equals(oldUser.getEmail())) {
            if (isEmailExists(newEmail)) {
                String errorMessage = "Этот email уже используется";
                log.error("Ошибка валидации обновления email пользователя: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
        }

        String newLogin = updatedUser.getLogin();
        if (!oldUser.getLogin().equals(newLogin)) {
            if (isLoginExists(newLogin)) {
                String errorMessage = "Логин: " + newLogin + " уже занят";
                log.error("Ошибка валидации обновления логина: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
        }

        String newName = updatedUser.getName();
        if (newName == null || newName.isBlank()) {
            oldUser.setName(newLogin);
        }

        LocalDate newBirthday = updatedUser.getBirthday();
        if (newBirthday != null) {
            if (newBirthday.isAfter(LocalDate.now())) {
                String errorMessage = "Дата рождения не может быть в будущем";
                log.error("Ошибка валидации обновления даты рождения: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
        }

        return userStorage.save(updatedUser);
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User friend = userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + friendId + " не найден"));

        friendDbStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + friendId + " не найден"));

        friendDbStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(long userId) {
        log.info("Запрос в сервис на получение списка друзей");
        log.info(userStorage.findById(userId).toString());
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка сервиса Пользователь с id: " + userId + " не найден"));

        List<User> friends = friendDbStorage.getUserFriends(userId);
        return friends;
    }

    public List<User> findCommonFriends(long userId, long otherUserId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User otherUser = userStorage.findById(otherUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + otherUserId + " не найден"));

        List<User> commonFriends = friendDbStorage.findCommonFriends(userId, otherUserId);
        log.trace("Список общих друзей пользователей {} и {}: {}", userId, otherUserId, commonFriends);
        return commonFriends;
    }

    private boolean isLoginExists(String login) {
        log.info("Проверка логина на уникальность");
        return findAll().stream()
                .map(User::getLogin)
                .anyMatch(existLogin -> existLogin.equals(login));
    }

    private boolean isEmptyEmail(String email) {
        log.info("Проверка на заполнение email");
        return email == null || email.isBlank();
    }

    private boolean isEmailExists(String email) {
        log.info("Проверка email на уникальность");
        return findAll().stream()
                .map(User::getEmail)
                .anyMatch(existEmail -> existEmail.equals(email));
    }

}
