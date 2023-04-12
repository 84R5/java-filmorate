package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    List<Film> getFilms();

    Film create(Film film);

    Film updateFilm(Film film);

    Film findFilmById(long id);

    void addLikeRate(Long filmId, Integer rate);

    void removeLikeRate(long filmId, int rate);

    List<Long> getCrossFilmsUserFromLike(long userId);

    Map<Long, Integer> getUserFilmsRateFromLikes(long userId);

    boolean isFilmExist(long filmId);

    List<Film> getFilmTop(Long count, Integer genreId, Integer year);

    List<Film> getRecommendations(long userId) throws NotFoundException;
}