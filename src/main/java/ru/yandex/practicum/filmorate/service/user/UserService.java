package ru.yandex.practicum.filmorate.service.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Data
@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User create(User user) {
        userStorage.create(user);
        return user;
    }

    public User update(User user) {
        if (userStorage.findUserById(user.getId()) != null) {
            return userStorage.update(user);
        } else {
            throw new NotFoundException("Пользователя с таким ID не существует");
        }
    }

    public User addFriend(Long userId, Long friendId) throws NotFoundException {
        final User user = userStorage.findUserById(userId.longValue());
        final User friend = userStorage.findUserById(friendId.longValue());

        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else if (friend == null) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        } else {
            userStorage.addFriend(userId, friendId);
            return userStorage.update(user);
        }
    }

    public User deleteFriend(Long userId, Long friendId) throws NotFoundException {
        User user = userStorage.findUserById(userId.longValue());
        User friend = userStorage.findUserById(friendId.longValue());

        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else if (friend == null) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        } else {
            userStorage.removeFriend(userId, friendId);
            return userStorage.update(user);
        }
    }

    public User get(Long userId) throws NotFoundException {
        final User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return user;
    }

    public Collection<User> getAll() {
        return userStorage.getUsers();
    }

    public List<User> getFriendsUser(Long userId) throws NotFoundException {
        User user = userStorage.findUserById(userId.longValue());
        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else {
            List<User> friendsList = userStorage.getFriends(userId.longValue());
            return friendsList;
        }
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) throws NotFoundException {
        User user = userStorage.findUserById(userId.longValue());
        User otherUser = userStorage.findUserById(otherUserId.longValue());

        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else if (otherUser == null) {
            throw new NotFoundException("User with id = " + otherUserId + " not found");
        } else {
            return userStorage.getCommonFriends(userId, otherUserId);
        }
    }
}
