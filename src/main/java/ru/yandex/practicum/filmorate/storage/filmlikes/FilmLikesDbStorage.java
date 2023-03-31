package ru.yandex.practicum.filmorate.storage.filmlikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.filmlikes.FilmLikesStorage;
import ru.yandex.practicum.filmorate.storage.genrefilm.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;


@Component
public class FilmLikesDbStorage implements FilmLikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmLikesDbStorage(JdbcTemplate jdbcTemplate, FilmGenreStorage filmGenreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = filmGenreStorage;
    }

    @Override
    public void saveLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO user_film_likes (USER_ID, FILM_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM user_film_likes WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }


    @Override
    public List<Film> getCount(int count) {
        String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, "
                + "f.RATING_MPAA_ID, COUNT(u.USER_ID) AS likes_count FROM film f "
                + "LEFT OUTER JOIN user_film_likes u ON f.FILM_ID = u.FILM_ID "
                + "WHERE NOT f.is_delete "
                + "GROUP BY f.FILM_ID ORDER BY likes_count DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }


    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {

        String sqlQuery =
                "SELECT f.* FROM film f "
                        + "INNER JOIN user_film_likes ufl1 ON ufl1.film_id = f.film_id "
                        + "INNER JOIN user_film_likes ufl2 ON ufl2.film_id = f.film_id "
                        + "WHERE ufl1.USER_ID = ? AND ufl2.USER_ID = ? "
                        + "AND NOT f.is_delete "
                        + "ORDER BY (SELECT COUNT(*) FROM user_film_likes ul WHERE ul.film_id = f.film_id) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int mpaId = resultSet.getInt("RATING_MPAA_ID");
        String sqlFindName = "SELECT RATING_MPAA_ID, NAME FROM rating_mpaa WHERE RATING_MPAA_ID = ?";
        MPA mpa = jdbcTemplate.queryForObject(sqlFindName, this::mapRowToMpa, mpaId);
        LinkedHashSet<Genre> setGenre = new LinkedHashSet<>();
        filmGenreStorage.getGenreList(resultSet.getLong("FILM_ID")).stream()
                .forEach(g -> setGenre.add(g));
        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .genres(setGenre)
                .mpa(mpa)
                .build();
    }

    private MPA mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return MPA.builder()
                .id(resultSet.getInt("RATING_MPAA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
