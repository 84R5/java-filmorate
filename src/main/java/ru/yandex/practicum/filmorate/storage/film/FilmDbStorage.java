package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.Constants.FILM_RATE_AV;

@Repository
@Qualifier
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rate, f.age_id, " +
                "GROUP_CONCAT(DISTINCT g.genre_id ORDER BY g.genre_id ASC SEPARATOR ',') AS genre_ids " +
                "FROM Film AS f " +
                "LEFT JOIN FilmGenre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN Genre AS g ON fg.genre_id = g.genre_id " +
                "GROUP BY f.film_id " +
                "ORDER BY f.film_id ASC";
        List<Film> films = new ArrayList<>();
        Map<Long, List<Genre>> genreMap = new HashMap<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            List<Genre> genres = new ArrayList<>();
            String genreIdsString = rs.getString("genre_ids");
            if (genreIdsString != null) {
                String[] genreIds = genreIdsString.split(",");
                for (String genreId : genreIds) {
                    genres.add(genreStorage.findGenreById(Integer.parseInt(genreId)));
                }
            }
            genreMap.put(filmId, genres);
            Film film = Film.builder()
                    .id(filmId)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(Objects.requireNonNull(rs.getDate("release_date")).toLocalDate())
                    .duration(rs.getLong("duration"))
                    .rate(rs.getInt("rate"))
                    .mpa(mpaStorage.findMPAById(rs.getInt("age_id")))
                    .build();
            films.add(film);
        }
        for (Film film : films) {
            film.setGenres(genreMap.get(film.getId()));
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("Film").usingGeneratedKeyColumns("film_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("rate", film.getRate());
        parameters.put("age_id", film.getMpa().getId());
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        film.setId(key.longValue());

        if (film.getGenres() != null) {
            String sql = "INSERT INTO FilmGenre (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(sql, film.getId(), genre.getId()));
        } else {
            film.setGenres(new ArrayList<>());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlId = "SELECT film_id FROM Film ORDER BY film_id DESC LIMIT 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlId);
        sqlRowSet.next();
        if (film.getId() > sqlRowSet.getInt("film_id") || film.getId() <= 0) {
            throw new NotFoundException("Фильм не найден.");
        } else {
            String sql = "UPDATE Film SET name=?, description=?, release_date=?, duration=?, rate=?, age_id=? WHERE film_id=?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getRate(), film.getMpa().getId(), film.getId());
            if (film.getGenres() != null) {
                Set<Integer> updatedGenreIds = new HashSet<>();
                film.getGenres().forEach(g -> updatedGenreIds.add(g.getId()));

                Set<Integer> existingGenreIds = new HashSet<>();
                String sqlGenreIds = "SELECT genre_id FROM FilmGenre WHERE film_id=?";
                SqlRowSet rsGenreIds = jdbcTemplate.queryForRowSet(sqlGenreIds, film.getId());
                while (rsGenreIds.next()) {
                    existingGenreIds.add(rsGenreIds.getInt("genre_id"));
                }

                Set<Integer> genreIdsToRemove = new HashSet<>(existingGenreIds);
                genreIdsToRemove.removeAll(updatedGenreIds);

                Set<Integer> genreIdsToAdd = new HashSet<>(updatedGenreIds);
                genreIdsToAdd.removeAll(existingGenreIds);

                if (!genreIdsToRemove.isEmpty()) {
                    String sqlDeleteGenres = "DELETE FROM FilmGenre WHERE film_id=? AND genre_id IN (%s)";
                    String genreIdsToRemoveStr = genreIdsToRemove.stream().map(String::valueOf).collect(Collectors.joining(", "));
                    String formattedSql = String.format(sqlDeleteGenres, genreIdsToRemoveStr);
                    jdbcTemplate.update(formattedSql, film.getId());
                }

                if (!genreIdsToAdd.isEmpty()) {
                    String sqlInsertGenre = "INSERT INTO FilmGenre VALUES (?, ?)";
                    genreIdsToAdd.forEach(genreId -> jdbcTemplate.update(sqlInsertGenre, film.getId(), genreId));
                }

                List<Genre> updatedGenres = new ArrayList<>();
                String sqlGenre = "SELECT DISTINCT genre_id FROM FilmGenre WHERE film_id=? ORDER BY genre_id ASC";
                SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlGenre, film.getId());
                while (rs.next()) {
                    updatedGenres.add(genreStorage.findGenreById(rs.getInt("genre_id")));
                }
                updatedGenres.sort(Comparator.comparingInt(Genre::getId));
                film.setGenres(updatedGenres);
            }
        }

        return film;
    }

    @Override
    public Film findFilmById(long id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rate, f.age_id, " +
                "GROUP_CONCAT(DISTINCT g.genre_id ORDER BY g.genre_id ASC SEPARATOR ',') AS genre_ids " +
                "FROM Film AS f " +
                "LEFT JOIN FilmGenre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN Genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id = ? " +
                "GROUP BY f.film_id";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (!rs.next()) {
            throw new NotFoundException("Фильм не найден.");
        }

        String genreIds = rs.getString("genre_ids");
        List<Genre> genres = new ArrayList<>();
        if (genreIds != null) {
            String[] genreIdArray = genreIds.split(",");
            for (String genreId : genreIdArray) {
                Genre genre = genreStorage.findGenreById(Integer.parseInt(genreId));
                genres.add(genre);
            }
        }

        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(Objects.requireNonNull(rs.getDate("release_date")).toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getInt("rate"))
                .genres(genres)
                .mpa(mpaStorage.findMPAById(rs.getInt("age_id")))
                .build();
    }

    @Override
    public boolean isFilmExist(long filmId) {
        String sql = "SELECT COUNT(*) FROM film WHERE film_id = ? ;";
        int filmCount = jdbcTemplate.queryForObject(sql, Integer.class, filmId);

        return filmCount > 0;
    }

    @Override
    public List<Film> getFilmTop(Long count, Integer genreId, Integer year) {
        List<Film> films;
        if (genreId == null && year == null) {
            films = getTopFilmByCount(count);
        } else if (genreId == null) {
            films = getTopFilmByCountYear(count, year);
        } else if (year == null) {
            films = getTopFilmByCountGenre(count, genreId);
        } else {
            films = getTopFilmByCountGenreYear(count, genreId, year);
        }

        return films;
    }

    @Override
    public List<Film> getRecommendations(long userId) throws NotFoundException {
        Map<Long, Integer> userFilmsRate = getUserFilmsRateFromLikes(userId);
        List<Long> crossFilmsUserFromLike = getCrossFilmsUserFromLike(userId);

        List<Film> recommendationFilms = new ArrayList<>();

        for (Long crossUserId : crossFilmsUserFromLike) {
            Map<Long, Integer> crossFilmRate = getUserFilmsRateFromLikes(crossUserId);
            if (countUserCrossFilm(crossFilmRate, userFilmsRate) == 0) {
                continue;
            }

            for (Map.Entry<Long, Integer> filmRate : crossFilmRate.entrySet()) {
                long filmId = filmRate.getKey();
                int rate = filmRate.getValue();

                if (rate >= FILM_RATE_AV && !userFilmsRate.containsKey(filmId)) {
                    recommendationFilms.add(findFilmById(filmId));
                }
            }
        }
        return recommendationFilms;
    }

    private int countUserCrossFilm(Map<Long, Integer> crossFilmRate, Map<Long, Integer> userFilmsRate) {
        int countCrossFilm = 0;

        for (Map.Entry<Long, Integer> filmRateEntry : userFilmsRate.entrySet()) {
            long filmId = filmRateEntry.getKey();
            if (crossFilmRate.containsKey(filmId)) {
                int originRate = filmRateEntry.getValue();
                int crossRate = crossFilmRate.get(filmId);
                if ((crossRate < FILM_RATE_AV && originRate < FILM_RATE_AV)
                        || (crossRate >= FILM_RATE_AV && originRate >= FILM_RATE_AV)) {
                    countCrossFilm++;
                }
            }
        }
        return countCrossFilm;
    }

    @Override
    public void addLikeRate(Long filmId, Integer rate) {
        String sql = "UPDATE film " +
                " SET likes = likes + 1 , " +
                " rate_score = rate_score + ? ," +
                " average_rate = CAST(rate_score + ? AS FLOAT) / (likes + 1)" +
                " WHERE film_id = ?; ";

        jdbcTemplate.update(sql, rate, rate, filmId);
    }

    @Override
    public void removeLikeRate(long filmId, int rate) {
        String sql = "UPDATE film " +
                " SET likes = likes - 1 , " +
                " rate_score = rate_score - ? ," +
                " average_rate = CASE " +
                "                    WHEN (likes - 1) = 0 THEN 0" +
                "                    ELSE CAST((rate_score - ?) AS FLOAT) / (likes - 1) " +
                "                END" +
                " WHERE film_id = ?; ";

        jdbcTemplate.update(sql, rate, rate, filmId);
    }

    @Override
    public List<Long> getCrossFilmsUserFromLike(long userId) {
        String sql = "SELECT user_id FROM Film_like WHERE user_id <> ? " +
                " AND film_id IN ( " +
                "  SELECT film_id FROM Film_like WHERE user_id = ? " +
                " );";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("user_id"),
                userId, userId);
    }

    @Override
    public Map<Long, Integer> getUserFilmsRateFromLikes(long userId) {
        return jdbcTemplate.query(
                "SELECT film_id, rate FROM Film_like WHERE user_id = ? ;",
                (rs) -> {
                    Map<Long, Integer> result = new HashMap<>();
                    while (rs.next()) {
                        result.put(rs.getLong("FILM_ID"), rs.getInt("RATE"));
                    }
                    return result;
                }, userId);
    }

    private List<Film> getTopFilmByCount(Long count) {
        String sql = "SELECT * FROM film " +
                "ORDER BY average_rate DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    private List<Film> getTopFilmByCountGenreYear(Long count, Integer genreId, Integer year) {
        String sql = "SELECT * FROM film WHERE " +
                "FILM_ID IN (SELECT film_id FROM FILM_GENRES WHERE genre_id = ?) " +
                "AND YEAR(release_date) = ? " +
                "ORDER BY average_rate DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, genreId, year, count);
    }

    private List<Film> getTopFilmByCountGenre(Long count, Integer genreId) {
        String sql = "SELECT * FROM film WHERE " +
                "FILM_ID IN (SELECT film_id FROM film_genres WHERE genre_id = ?) " +
                "ORDER BY average_rate DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, genreId, count);
    }

    private List<Film> getTopFilmByCountYear(Long count, Integer year) {
        String sql = "SELECT * FROM film WHERE " +
                "YEAR(RELEASE_DATE) = ? " +
                "ORDER BY average_rate DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::makeFilm, year, count);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        List<Genre> genreList = new ArrayList<>();
        if (rs.getInt("genre_id") > 0) {
            genreList.add(genreStorage.findGenreById(rs.getInt("genre_id")));
        }
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getInt("rate"))
                .averageRate(rs.getFloat("average_rate"))
                .genres(genreList)
                .likes(rs.getLong("likes"))
                .mpa(mpaStorage.findMPAById(rs.getInt("age_id")))
                .build();
    }
}