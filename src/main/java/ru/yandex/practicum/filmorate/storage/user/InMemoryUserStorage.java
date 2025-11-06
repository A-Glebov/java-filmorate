package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        Long id = getNextId();
        user.setId(id);
        log.trace("Пользователю присвоен id {}", id);
        users.put(id, user);
        log.info("Пользователь id {} добавлен в хранилище", id);
        return user;
    }

    @Override
    public User save(User user) {
        log.info("Запрос на добавление пользователя в хранилище {}", user.getId());
        users.put(user.getId(), user);
        log.info("Данные пользователя id {} обновлены в хранилище", user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(long userId) {
        log.info("Запрос на получение пользователя по id из хранилища");
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        log.info("Запрос на получение всех пользователей из хранилища");
        return new ArrayList<>(users.values());
    }

    private long getNextId() {
        log.info("Генерация id пользователя");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("id пользователя сгенерирован");
        return ++currentMaxId;
    }

}
