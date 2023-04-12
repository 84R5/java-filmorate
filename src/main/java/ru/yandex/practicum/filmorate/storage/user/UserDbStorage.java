package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
@Qualifier
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM Users";
        return jdbcTemplate.query(sql, new UserMapper(jdbcTemplate));
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("Users").usingGeneratedKeyColumns("user_id");
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
        String sqlId = "SELECT user_id FROM Users ORDER BY user_id DESC LIMIT 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlId);
        sqlRowSet.next();
        if (user.getId() > sqlRowSet.getInt("user_id") || user.getId() <= 0) {
            throw new NotFoundException("Пользователь не найден.");
        }
        String sql = "UPDATE Users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User findUserById(long id) {
        String sqlId = "SELECT user_id FROM Users ORDER BY user_id DESC LIMIT 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlId);
        sqlRowSet.next();
        if (id > sqlRowSet.getInt("user_id") || id <= 0) {
            throw new NotFoundException("Пользователь не найден.");
        }
        String sql = "SELECT * FROM Users WHERE user_id = ?";
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
    public List<User> getFriends(long id) {
        String sql = "SELECT * FROM friendship WHERE user_id = ? AND status = 'ACCEPTED'";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        List<User> friends = new ArrayList<>();
        while (rs.next()) {
            friends.add(findUserById(rs.getInt("friend_id")));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        List<User> commonFriends = new ArrayList<>(getFriends(userId));
        commonFriends.retainAll(getFriends(otherId));
        return commonFriends;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO Friendship (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, "ACCEPTED");
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM Friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public boolean isUserExist(long userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ? ;";
        int countUser = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        return countUser > 0;
    }
}