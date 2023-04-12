package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

public interface FilmLikeStorage {
    void addLike(long filmId, long userId, int rate) throws NotFoundException;

    void removeLike(long filmId, long userId) throws NotFoundException;

    int getUserLikeRate(long filmId, long userId) throws NotFoundException;

    int getUserLikeCount(long filmId, long userId) throws NotFoundException;
}
