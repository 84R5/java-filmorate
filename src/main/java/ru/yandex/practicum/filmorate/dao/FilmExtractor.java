package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<Film> films = new LinkedHashSet<>();
        List<Genre> genres = new ArrayList<>();
        List<Director> directors = new ArrayList<>();
        while (rs.next()) {
            Long id = rs.getLong("FILM_ID");
            String name = rs.getString("NAME");
            String description = rs.getString("DESCRIPTION");
            LocalDate releaseDate = rs.getDate("RELEASE_DATE").toLocalDate();
            int rate = rs.getInt("RATE");
            Long duration = rs.getLong("DURATION");
            MPA mpa = new MPA(rs.getLong("AGE_ID"), rs.getString("AGE_NAME"));
            Genre genre = new Genre(rs.getLong("GENRE_ID"), rs.getString("GENRE_NAME"));
            Director director = new Director(rs.getLong("DIRECTOR_ID"), rs.getString("DIRECTOR_NAME"));
            Film film = Film.builder()
                    .id(id)
                    .mpa(mpa)
                    .name(name)
                    .description(description)
                    .releaseDate(releaseDate)
                    .duration(duration)
                    .rate(rate)
                    .build();
            if (films.add(film)) {
                directors = new ArrayList<>();
                genres = new ArrayList<>();
                film.setGenres(genres);
                film.setDirectors(directors);
            }
            if (director.getId() != null && director.getId() != 0 && !directors.contains(director)) {
                directors.add(director);
            }
            if (genre.getId() != null && genre.getId() != 0 && !genres.contains(genre)) {
                genres.add(genre);
            }
        }
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(films);
    }
}
