package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(final long filmId, final long userId, final int rate) throws NotFoundException {
        if (checkLike(filmId, userId) == 0) {
            String sql = "INSERT INTO Film_like (user_id, film_id, rate) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, filmId, userId, rate);
        }
    }

    @Override
    public void removeLike(final long filmId, final long userId) throws NotFoundException {
        if (checkLike(filmId, userId) > 0) {
            String sql = "DELETE FROM Film_like WHERE film_id = ? AND user_id = ? ;";
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    private int checkLike(final long filmId, final long userId) throws NotFoundException {
        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException("Film Id for like not found.");
        }
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException("User Id for like not found.");
        }
        String sql = "SELECT COUNT(*) FROM Film_like WHERE film_id = ? AND user_id = ? LIMIT 1;";

        return jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
    }

    @Override
    public int getUserLikeRate(long filmId, long userId) throws NotFoundException {
        if (checkLike(filmId, userId) > 0) {
            String sql = "SELECT rate FROM Film_like WHERE film_id = ? AND user_id = ? ;";
            return jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        }
        return 0;
    }

    @Override
    public int getUserLikeCount(long filmId, long userId) throws NotFoundException {
        return checkLike(filmId, userId);
    }
}
