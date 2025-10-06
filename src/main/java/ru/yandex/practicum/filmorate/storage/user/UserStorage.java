package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {


    List<User> findAll();

    User create(User user);

    User delete(User user);

    User save(User user);

    Optional<User> findById(long userId);

}
