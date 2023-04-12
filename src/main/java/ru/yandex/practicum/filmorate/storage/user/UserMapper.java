package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserMapper implements RowMapper<User> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        int userId = rs.getInt("user_id");
        String sql = "SELECT film_id FROM Film_like WHERE user_id = ?";
        List<Long> filmIds = jdbcTemplate.queryForList(sql, new Object[]{userId}, Long.class);
        Set<Long> filmsLike = new HashSet<>(filmIds);
        user.getFilmsLike().addAll(filmsLike);

        return user;
    }
}