package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User create(User user);

    User update(User user);

    User findUserById(long id);

    List<User> getFriends(long id);

    List<User> getCommonFriends(long id, long otherId);

    void addFriend(long id, long friendId);

    void removeFriend(long id, long friendId);

    void addFilmsLike(long filmId, long userId);

    void removeFilmLike(long filmId, long userId);
}
