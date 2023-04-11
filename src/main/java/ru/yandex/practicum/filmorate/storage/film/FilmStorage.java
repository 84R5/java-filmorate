package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    List<Film> getFilms();

    Film create(Film film);

    Film updateFilm(Film film);

    Film findFilmById(long id);

    void addFilmsLike(long filmId, long userId);

    void removeFilmLike(long filmId, long userId);

    List<Long> getCrossFilmsUserFromLike(long userId);

    Map<Long, Integer> getUserFilmsRateFromLikes(long userId);
}