package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GenreStorage {

    public final JdbcTemplate jdbcTemplate;
    private final List<Genre> genreList = new ArrayList<>();

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String sql = "SELECT * FROM Genre";
        genreList.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"),
                rs.getString("name"))));
    }

    public Genre findGenreById(int id) {
        String sql = "SELECT * FROM Genre " +
                "WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        if (genres.isEmpty()) {
            return null;
        }
        return genres.get(0);
    }

    public List<Genre> getGenreList() {
        return genreList;
    }

    public List<Genre> getFilmGenres(long filmId) {
        String sql = "SELECT * FROM Genre WHERE genre_id IN (SELECT genre_id FROM FilmGenre WHERE film_id = ?) " +
                " ORDER BY genre_id;";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("name")), filmId);
    }

    public List<Genre> getGenresByFilm(Long filmId) {
        String sql = "SELECT * " +
                "FROM genre AS g " +
                "WHERE g.genre_id IN (SELECT genre_id " +
                "FROM FilmGenre " +
                "WHERE film_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getLong("genre_id"),
                rs.getString("name"));
    }
}