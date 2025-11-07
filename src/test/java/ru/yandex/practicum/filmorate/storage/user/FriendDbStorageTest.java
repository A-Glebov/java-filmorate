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

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({UserDbStorage.class,
        UserRowMapper.class,
        FriendDbStorage.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendDbStorageTest {

    @Autowired
    private FriendDbStorage friendDbStorage;
    @Autowired
    private UserDbStorage userDbStorage;

    private User testUser;
    private User friendTestUser;
    private User commonTestFriend;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));

        friendTestUser = new User();
        friendTestUser.setEmail("testFriend@mail.ru");
        friendTestUser.setLogin("testFriendLogin");
        friendTestUser.setName("Test FriendUser");
        friendTestUser.setBirthday(LocalDate.of(1992, 2, 2));

        commonTestFriend = new User();
        commonTestFriend.setEmail("commonFriend@mail.ru");
        commonTestFriend.setLogin("commonFriendLogin");
        commonTestFriend.setName("Common FriendUser");
        commonTestFriend.setBirthday(LocalDate.of(1993, 2, 2));

        userDbStorage.create(testUser);
        userDbStorage.create(friendTestUser);
        userDbStorage.create(commonTestFriend);
    }

    @Test
    public void testAddFriend() {
        friendDbStorage.addFriend(testUser.getId(), friendTestUser.getId());

        assertThat(friendDbStorage.getUserFriends(testUser.getId()).size()).isEqualTo(1);
        assertThat(friendDbStorage.getUserFriends(friendTestUser.getId()).size()).isEqualTo(0);
    }

    @Test
    public void testBothUsersAreFriends() {
        friendDbStorage.addFriend(testUser.getId(), friendTestUser.getId());
        friendDbStorage.addFriend(friendTestUser.getId(), testUser.getId());

        assertThat(friendDbStorage.getUserFriends(testUser.getId()).size()).isEqualTo(1);
        assertThat(friendDbStorage.getUserFriends(friendTestUser.getId()).size()).isEqualTo(1);
    }

    @Test
    public void testRemoveFriend() {
        friendDbStorage.addFriend(testUser.getId(), friendTestUser.getId());
        assertThat(friendDbStorage.getUserFriends(testUser.getId()).size()).isEqualTo(1);

        friendDbStorage.deleteFriend(testUser.getId(), friendTestUser.getId());
        assertThat(friendDbStorage.getUserFriends(testUser.getId()).size()).isEqualTo(0);
    }

    @Test
    public void testFindCommonFriends() {
        friendDbStorage.addFriend(testUser.getId(), commonTestFriend.getId());
        friendDbStorage.addFriend(friendTestUser.getId(), commonTestFriend.getId());

        assertThat(friendDbStorage
                .findCommonFriends(testUser.getId(), friendTestUser.getId()).size()).isEqualTo(1);
    }

}