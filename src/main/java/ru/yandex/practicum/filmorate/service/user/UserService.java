package ru.yandex.practicum.filmorate.service.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

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
        final User user = userStorage.findUserById(userId);
        final User friend = userStorage.findUserById(friendId);

        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else if (friend == null) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        } else {
            userStorage.addFriend(userId, friendId);
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.ADD).eventType(FeedEventType.FRIEND).entityId(friendId).userId(userId).build());
            return userStorage.update(user);
        }
    }

    public void deleteFriend(Long userId, Long friendId) throws NotFoundException {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else if (friend == null) {
            throw new NotFoundException("User with id = " + friendId + " not found");
        } else {
            userStorage.removeFriend(userId, friendId);
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.REMOVE).eventType(FeedEventType.FRIEND).entityId(friendId).userId(userId).build());
            userStorage.update(user);
        }
    }

    public void deleteUserById(Long userId) {
        userStorage.deleteUserById(userId);
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
        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else {
            return userStorage.getFriends(userId);
        }
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) throws NotFoundException {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherUserId);

        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else if (otherUser == null) {
            throw new NotFoundException("User with id = " + otherUserId + " not found");
        } else {
            return userStorage.getCommonFriends(userId, otherUserId);
        }
    }

    public List<Feed> getFeed(Long userId) {
        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        } else {
            return feedStorage.getFeed(userId);
        }
    }

    public Collection<Film> getRecommendations(long userId) {
        return filmStorage.getRecommendations(userId);
    }
}
