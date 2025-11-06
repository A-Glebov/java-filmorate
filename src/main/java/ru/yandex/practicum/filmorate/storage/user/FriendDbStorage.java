package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.user.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public void addFriend(long userId, long friendId) {
        String query = "INSERT INTO friendship(user_id, friend_id) VALUES (?,?)";
        jdbcTemplate.update(query, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId/*, userId, friendId*/);
    }

    public List<User> getUserFriends(long userId) {
        log.info("Запрос в хранилище на получение списка друзей");
        String sql = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM friendship AS f " +
                "INNER JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? " +
                "ORDER BY u.user_id";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    public List<User> findCommonFriends(long userId, long otherUserId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM friendship AS f " +
                "INNER JOIN friendship fr ON fr.friend_id = f.friend_id " +
                "INNER JOIN users u ON u.user_id = fr.friend_id " +
                "WHERE f.user_id = ? AND fr.user_id = ? " +
                "AND f.friend_id <> fr.user_id AND fr.friend_id <> f.user_id";
        return jdbcTemplate.query(sql, userRowMapper, userId, otherUserId);
    }
}
