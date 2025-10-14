package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Optional<User> findById(long userId) {
        return userStorage.findById(userId);
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
        Long id = updatedUser.getId();

        if (id == null) {
            String errorMessage = "Id должен быть указан";
            log.error("Ошибка валидации обновления данных пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        User oldUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));

        String newEmail = updatedUser.getEmail();
        if (!newEmail.equals(oldUser.getEmail())) {
            if (isEmailExists(newEmail)) {
                String errorMessage = "Этот email уже используется";
                log.error("Ошибка валидации обновления email пользователя: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            oldUser.setEmail(newEmail);
        }

        String newLogin = updatedUser.getLogin();
        if (!oldUser.getLogin().equals(newLogin)) {
            if (isLoginExists(newLogin)) {
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

        LocalDate newBirthday = updatedUser.getBirthday();
        if (newBirthday != null) {
            if (newBirthday.isAfter(LocalDate.now())) {
                String errorMessage = "Дата рождения не может быть в будущем";
                log.error("Ошибка валидации обновления даты рождения: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            oldUser.setBirthday(updatedUser.getBirthday());
        }
        return oldUser;
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User friend = userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id: " + friendId + " не найден"));

        log.trace("Список друзей userId {} до добавления нового друга: {}", userId, user.getFriends());
        log.trace("Список друзей friendId {} до добавления нового друга: {}", friendId, friend.getFriends());
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.trace("Список друзей userId {} после добавления нового друга: {}", userId, user.getFriends());
        log.trace("Список друзей friendId {} после добавления нового друга: {}", friendId, friend.getFriends());
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + friendId + " не найден"));

        log.trace("Список друзей userId {} до удаления друга: {}", userId, user.getFriends());
        log.trace("Список друзей friendId {} до удаления друга: {}", friendId, friend.getFriends());
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.trace("Список друзей userId {} после удаления друга: {}", userId, user.getFriends());
        log.trace("Список друзей friendId {} после удаления друга: {}", friendId, friend.getFriends());
    }

    public List<User> getUserFriends(long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        List<User> friends = user.getFriends().stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Ошибка при получении списка друзей"))).toList();
        log.trace("Список друзей userId {}: ", user.getFriends());
        return friends;
    }

    public List<User> findCommonFriends(long userId, long otherUserId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User otherUser = userStorage.findById(otherUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + otherUserId + " не найден"));

        Set<Long> setCommonFriends = user.getFriends();
        Set<Long> friendsOtherUser = otherUser.getFriends();

        setCommonFriends.retainAll(friendsOtherUser);

        List<User> commonFriends = setCommonFriends.stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Ошибка получения списка общих друзей")))
                .toList();
        log.trace("Список общих друзей пользователей {} и {}: {}", userId, otherUserId, commonFriends);
        return commonFriends;
    }

    private boolean isLoginExists(String login) {
        return findAll().stream()
                .map(User::getLogin)
                .anyMatch(existLogin -> existLogin.equals(login));
    }

    private boolean isEmptyEmail(String email) {
        return email == null || email.isBlank();
    }

    private boolean isEmailExists(String email) {
        return findAll().stream()
                .map(User::getEmail)
                .anyMatch(existEmail -> existEmail.equals(email));
    }

}
