package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    UserService userService;
    InMemoryUserStorage inMemoryUserStorage;
    User user;
    User updateUser;

    @BeforeEach
    public void init() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        user = User.builder()
                .email("pochta@ya.ru")
                .login("login-user1")
                .name("Ivanov")
                .birthday(LocalDate.of(2000, 1, 1))
                .friends(new HashSet<>())
                .build();

        updateUser = User.builder()
                .email("pochtaUpdate@ya.ru")
                .login("login-user2")
                .name("Ivanov-Petrovsky")
                .birthday(LocalDate.of(2000, 1, 2))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    public void createUserWithValidFieldsAndFindAll() {
        userService.create(user);

        assertEquals(1, user.getId(), "Юзеру не присвоен id");
        assertEquals(1, userService.findAll().size(), "Юзер не добавлен");
    }

    @Test
    public void createUserWithExistLogin() {
        User userWithExistLogin = User.builder()
                .email("pochta@mail.ru")
                .login("login-user1")
                .name("Petrov")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userService.create(user);

        assertThrows(ValidationException.class,
                () -> userService.create(userWithExistLogin),
                "Добавление пользователя с существующим логином должно приводить к исключению");
    }

    @Test
    public void createUserWithEmptyName() {
        user.setName("");
        userService.create(user);

        assertEquals(user.getName(), user.getLogin(), "При пустом имени полю name должен присваиваться login");
    }

    @Test
    public void updateUserWitNonExistentId() {
        updateUser.setId(2L);
        assertThrows(NotFoundException.class,
                () -> userService.update(updateUser),
                "Обновление юзера с несуществующим id должно приводить к исключению");
    }

    @Test
    public void updateFileWithoutId() {
        assertThrows(ValidationException.class,
                () -> userService.update(updateUser), "id не должен равняться null");
    }

    @Test
    public void updateUser() {
        userService.create(user);
        updateUser.setId(1L);
        userService.update(updateUser);

        assertEquals(updateUser, user, "Данные пользователя не обновились");
    }

    @AfterEach
    public void clean() {
        inMemoryUserStorage = null;
    }

}

