package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController userController = new UserController();
    User user;
    User updateUser;

    @BeforeEach
    public void init() {
        user = User.builder()
                .email("pochta@ya.ru")
                .login("login-user1")
                .name("Ivanov")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        updateUser = User.builder()
                .email("pochtaUpdate@ya.ru")
                .login("login-user2")
                .name("Ivanov-Petrovsky")
                .birthday(LocalDate.of(2000, 1, 2))
                .build();
    }

    @Test
    public void createUserWithValidFieldsAndFindAll() {
        userController.create(user);

        assertEquals(1, user.getId(), "Юзеру не присвоен id");
        assertEquals(1, userController.findAll().size(), "Юзер не добавлен");
    }

    @Test
    public void createUserWithExistLogin() {
        User userWithExistLogin = User.builder()
                .email("pochta@mail.ru")
                .login("login-user1")
                .name("Petrov")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        assertThrows(ValidationException.class,
                () -> userController.create(userWithExistLogin),
                "Добавление пользователя с существующим логином должно приводить к исключению");

    }

    @Test
    public void createUserWithEmptyName() {
        user.setName("");
        userController.create(user);

        assertEquals(user.getName(), user.getLogin(), "При пустом имени полю name должен присваиваться login");
    }

    @Test
    public void updateUserWitNonExistentId() {
        updateUser.setId(2L);
        assertThrows(NotFoundException.class,
                () -> userController.update(updateUser),
                "Обновление юзера с несуществующим id должно приводить к исключению");
    }

    @Test
    public void updateFileWithoutId() {
        assertThrows(ValidationException.class,
                () -> userController.update(updateUser), "id не должен равняться null");
    }

    @Test
    public void updateUser() {
        userController.create(user);
        updateUser.setId(1L);
        userController.update(updateUser);

        assertEquals(updateUser, user, "Данные пользователя не обновились");
    }

}

