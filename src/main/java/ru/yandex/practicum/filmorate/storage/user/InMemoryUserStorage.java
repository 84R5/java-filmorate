package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static long id = 0;
    private final HashMap<Long, User> users = new HashMap<>();

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (!users.containsKey(user.getId())) {
            id++;
            user.setId(id);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь не найден.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return users.get(id);
    }

    @Override
    public List<User> getFriends(long id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        return null;
    }

    @Override
    public void addFriend(long id, long friendId) {

    }

    @Override
    public void removeFriend(long id, long friendId) {

    }

    @Override
    public void addFilmsLike(long filmId, long userId) {

    }

    @Override
    public void removeFilmLike(long filmId, long userId) {

    }
}
