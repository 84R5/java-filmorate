package ru.yandex.practicum.filmorate.service.film;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
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
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

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
            log.warn("Error receiving the movie. Film with ID " + id + " not found");
            throw new NotFoundException("Film with ID " + id + " not found");
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
            log.warn("Sorting is possible either by year or by the number of likes");
            throw new IllegalArgumentException("Sorting is possible either by year or by the number of likes");
        }

        if (directorStorage.getDirector(directorId) == null) {
            log.warn("Director not found");
            throw new IllegalArgumentException("Director not found");
        }

        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getSearchFilms(String query, String by, int count) {
        return filmStorage.getSearchFilms(query, by, count);
    }

    public Genre getGenreById(int id) {
        if (genreStorage.findGenreById(id) != null) {
            return genreStorage.findGenreById(id);
        } else {
            log.warn("Genre not found");
            throw new NotFoundException("Genre not found");
        }
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getGenreList();
    }

    public List<MPA> getAllMPA() {
        return mpaStorage.getMPAList();
    }

    public MPA getMPAById(int id) {
        if (mpaStorage.findMPAById(id) != null) {
            return mpaStorage.findMPAById(id);
        } else {
            log.warn("Age rating not found");
            throw new NotFoundException("Age rating not found");
        }
    }

}
