package ru.yandex.practicum.filmorate.service.film;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;


@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;
    private final DirectorStorage directorStorage;

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
        Film receivedFilm = filmStorage.findFilmById(id);
        if (receivedFilm != null) {
            return receivedFilm;
        } else {
            log.warn("Ошибка получения фильма. Фильм с ID " + id + " не найден");
            throw new NotFoundException("Фильм с Id " + id + " не найден");
        }
    }

    public void deleteFilmById(long filmId) {
        filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getPopular(int count, Integer genreId, Integer year) {
        return filmStorage.getPopular(count, genreId, year);
    }


    public void addFilmLike(long filmId, long userId) {
        userStorage.addFilmsLike(filmId, userId);
        Film film = filmStorage.findFilmById(filmId);
        film.setRate(film.getRate() + 1);
        userStorage.findUserById(userId).getFilmsLike().add(filmId);
        feedStorage.addFeed(Feed.builder()
                .operation(FeedOperation.ADD)
                .eventType(FeedEventType.LIKE)
                .entityId(filmId)
                .userId(userId)
                .build());
    }

    public void removeFilmLike(long filmId, long userId) {
        userStorage.removeFilmLike(filmId, userId);
        Film film = filmStorage.findFilmById(filmId);
        film.setRate(film.getRate() - 1);
        userStorage.findUserById(userId).getFilmsLike().remove(filmId);
        feedStorage.addFeed(Feed.builder()
                .operation(FeedOperation.REMOVE)
                .eventType(FeedEventType.LIKE)
                .entityId(filmId)
                .userId(userId)
                .build());
    }

    public List<Film> getDirectorFilms(Long directorId, String sortBy) {

        if (!(sortBy.equals("year") || sortBy.equals("likes"))) {
            throw new IllegalArgumentException("Сортировка возможна либо по годам, либо по количеству лайков");
        }

        if (directorStorage.getDirector(directorId) == null) {
            throw new IllegalArgumentException("Режиссер не найден.");
        }

        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getSearchFilms(String query, String by, int count) {
        return filmStorage.getSearchFilms(query, by, count);
    }
}
