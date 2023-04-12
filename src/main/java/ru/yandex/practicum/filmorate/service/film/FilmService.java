package ru.yandex.practicum.filmorate.service.film;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeDbStorage filmLikeStorage;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }

    public List<Film> getTop(Long count, Integer genreId, Integer year) throws NotFoundException {
        return filmStorage.getFilmTop(count, genreId, year);
    }

    public void addLike(Long filmId, Long userId, Integer rate) throws NotFoundException {
        if (filmLikeStorage.getUserLikeCount(filmId, userId) != 0) {
            filmStorage.removeLikeRate(filmId, filmLikeStorage.getUserLikeRate(filmId, userId));
            filmLikeStorage.removeLike(filmId, userId);
        }
        filmLikeStorage.addLike(filmId, userId, rate);
        filmStorage.addLikeRate(filmId, rate);
    }

    public void removeFilmLike(long filmId, long userId) {
        filmStorage.removeLikeRate(filmId, filmLikeStorage.getUserLikeRate(filmId, userId));
        filmLikeStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getRecommendation(Long userId) throws NotFoundException {
        return filmStorage.getRecommendations(userId);
    }
}
