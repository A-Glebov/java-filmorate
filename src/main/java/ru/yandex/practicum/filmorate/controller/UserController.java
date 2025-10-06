package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на создание пользователя");
        user = userService.create(user);
        log.info("Пользователь id: {} создан", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        log.info("Запрос на обновление данных пользователя");
        User user = userService.update(updatedUser);
        log.info("Данные пользователя обновлены");
        return user;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId,
                          @PathVariable long friendId) {
        log.info("Запрос на добавление в список друзей пользователя {} пользователя {}", userId, friendId);
        userService.addFriend(userId, friendId);
        log.info("Пользователь id {} успешно добавил друга id {}", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId,
                             @PathVariable long friendId) {
        log.info("Запрос на удаление из друзей");
        userService.deleteFriend(userId, friendId);
        log.info("Пользователь id {} успешно удален из друзей пользователя id {}", friendId, userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable long userId) {
        log.info("Запрос на получение списка друзей пользователя id: {}", userId);
        List<User> friends = userService.getUserFriends(userId);
        log.info("Список друзей пользователя id: {} успешно получен", userId);
        return friends;
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> findCommonFriends(@PathVariable long userId,
                                        @PathVariable long otherUserId) {
        log.info("Запрос на получение списка общих друзей пользователя id: {} с пользователем id: {}",
                userId, otherUserId);
        List<User> friends = userService.findCommonFriends(userId, otherUserId);
        log.info("Список общих друзей пользователя id: {} с пользователем id : {} успешно получен : {}",
                userId, otherUserId, friends);

        return friends;
    }

}
