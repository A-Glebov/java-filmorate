package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.user.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({UserDbStorage.class,
        UserRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testCreateUser() {
        User createdUser = userStorage.create(testUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("test@mail.ru");
        assertThat(createdUser.getLogin()).isEqualTo("testlogin");
        assertThat(createdUser.getName()).isEqualTo("Test User");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testUserNotFound() {
        Optional<User> userOptional = userStorage.findById(2);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testFindById() {
        User createdUser = userStorage.create(testUser);
        User findUser = userStorage.findById(createdUser.getId()).get();

        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isPositive();
        assertThat(findUser.getLogin()).isEqualTo("testlogin");
        assertThat(findUser.getName()).isEqualTo("Test User");
        assertThat(findUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testUpdateUser() {
        User createdUser = userStorage.create(testUser);
        User updatedUser = new User();
        updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updatedUser@mail.ru");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 2, 2));

        userStorage.save(updatedUser);
        User findUpdatedUser = userStorage.findById(updatedUser.getId()).get();

        assertThat(findUpdatedUser).isNotNull();
        assertThat(findUpdatedUser.getId()).isPositive();
        assertThat(findUpdatedUser.getEmail()).isEqualTo("updatedUser@mail.ru");
        assertThat(findUpdatedUser.getLogin()).isEqualTo("updatedlogin");
        assertThat(findUpdatedUser.getName()).isEqualTo("Updated User");
        assertThat(findUpdatedUser.getBirthday()).isEqualTo(LocalDate.of(1991, 2, 2));
    }

    @Test
    public void testUpdatedNoSuchUser() {
        User testUser = new User();
        testUser.setId(999L);
        userStorage.save(testUser);

        Optional<User> userOptional = userStorage.findById(testUser.getId());
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testFindAll() {
        User createdUser = userStorage.create(testUser);
        User updatedUser = new User();
        updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("updatedUser@mail.ru");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 2, 2));

        userStorage.create(updatedUser);
        List<User> users = userStorage.findAll();

        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(2);
    }

}