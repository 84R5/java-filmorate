package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Qualifier
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");

        Long directorId = insert.executeAndReturnKey(toMap(director)).longValue();
        director.setId(directorId);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name =? " +
                "WHERE director_id =?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {

        String sql = "DELETE " +
                "FROM director_films " +
                "WHERE director_id =?";
        jdbcTemplate.update(sql, directorId);

        sql = "DELETE " +
                "FROM directors " +
                "WHERE director_id =?";
        jdbcTemplate.update(sql, directorId);
    }

    @Override
    public Director getDirector(Long id) {
        String sql = "SELECT * " +
                "FROM directors " +
                "WHERE director_id =?";
        List<Director> directors = jdbcTemplate.query(sql, ((rs, rowNum) -> makeDirector(rs)), id);

        if (directors.isEmpty()) {
            throw new NotFoundException("Режиссер с идентификатором: " + id + " не существует.");
        }

        return directors.get(0);
    }

    @Override
    public Iterable<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeDirector(rs)));
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> directorParameters = new HashMap<>();

        directorParameters.put("name", director.getName());

        return directorParameters;
    }

    public List<Director> findDirectorsByFilm(long filmId) {
        String sql = "SELECT * " +
                "FROM directors " +
                "WHERE director_id = (SELECT director_id " +
                "FROM director_films " +
                "WHERE film_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId((long) rs.getInt("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}