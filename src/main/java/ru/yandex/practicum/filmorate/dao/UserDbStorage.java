package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * " +
                "FROM Users";
        return jdbcTemplate.query(sql, new UserMapper(jdbcTemplate));
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert
                .withTableName("Users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        parameters.put("email", user.getEmail());
        parameters.put("birthday", user.getBirthday());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        user.setId(key.longValue());

        return user;
    }

    @Override
    public User update(User user) {
        String sqlId = "SELECT user_id " +
                "FROM Users " +
                "ORDER BY user_id DESC " +
                "LIMIT 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlId);

        sqlRowSet.next();
        if (user.getId() > sqlRowSet.getInt("user_id") || user.getId() <= 0) {
            throw new NotFoundException("Пользователь не найден.");
        }

        String sql = "UPDATE Users " +
                "SET email=?, login=?, name=?, birthday=? " +
                "WHERE user_id=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User findUserById(long id) {
        String sqlId = "SELECT user_id " +
                "FROM Users " +
                "ORDER BY user_id DESC " +
                "LIMIT 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlId);

        sqlRowSet.next();
        if (id > sqlRowSet.getInt("user_id") || id <= 0) {
            throw new NotFoundException("Пользователь не найден.");
        }

        String sql = "SELECT * " +
                "FROM Users " +
                "WHERE user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        rs.next();

        Set<Long> idList = new HashSet<>();
        for (User friend : getFriends(id)) {
            idList.add(friend.getId());
        }

        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .friends(idList)
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .build();
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT * " +
                "FROM users AS u, friendship AS f " +
                "WHERE u.user_id = f.friend_id AND f.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        String sql = "SELECT * " +
                "FROM users AS u, friendship AS f, friendship AS o "
                + "WHERE u.user_id = f.friend_id "
                + "AND u.user_id = o.friend_id "
                + "AND f.user_id= ? "
                + "AND o.user_id=?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherUserId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT " +
                "INTO Friendship (user_id, friend_id, status) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, "ACCEPTED");
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE " +
                "FROM Friendship " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void addFilmsLike(long filmId, long userId) {

        String sql = "INSERT INTO Film_like (user_id, film_id) " +
                "SELECT ?, ? " +
                "WHERE NOT EXISTS ( " +
                "SELECT 1  " +
                "FROM Film_like " +
                "WHERE user_id =? AND film_id = ?)";
        jdbcTemplate.update(sql, userId, filmId, userId, filmId);

        String sqlCheck = "SELECT 1 " +
                "FROM Film_like " +
                "WHERE user_id =? AND film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlCheck, userId, filmId);
        if (!sqlRowSet.next()) {
            String sqlRate = "UPDATE Film " +
                    "SET rate=? " +
                    "WHERE film_id= ?";
            jdbcTemplate.update(sqlRate, +1, userId);
        }

    }

    @Override
    public void removeFilmLike(long filmId, long userId) {
        String sql = "DELETE " +
                "FROM Film_like " +
                "WHERE user_id=? AND film_id=?";
        jdbcTemplate.update(sql, userId, filmId);

        String sqlRate = "UPDATE Film " +
                "SET rate=? " +
                "WHERE film_id=?";
        jdbcTemplate.update(sqlRate, -1, userId);
    }

    @Override
    public void deleteUserById(Long userId) {

        String sql = "DELETE FROM Friendship " +
                "WHERE friend_id =?";
        jdbcTemplate.update(sql, userId);

        sql = "DELETE FROM Friendship " +
                "WHERE user_id =?";
        jdbcTemplate.update(sql, userId);

        sql = "DELETE FROM Film_like " +
                "WHERE user_id =?";
        jdbcTemplate.update(sql, userId);

        sql = "DELETE FROM review_likes " +
                "WHERE user_id =?";
        jdbcTemplate.update(sql, userId);

        sql = "DELETE FROM reviews " +
                "WHERE user_id =?";
        jdbcTemplate.update(sql, userId);

        sql = "DELETE FROM user_feed " +
                "WHERE user_id =?";
        jdbcTemplate.update(sql, userId);

        sql = "DELETE FROM Users " +
                "WHERE user_id =?";
        jdbcTemplate.update(sql, userId);
    }

    public User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .build();
    }
}