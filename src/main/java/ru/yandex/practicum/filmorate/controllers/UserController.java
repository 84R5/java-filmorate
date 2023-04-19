package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Входящий запрос на получение списка всех пользователей");
        return userService.getAll();
    }


    @PostMapping
    public User create(@RequestBody @Validated User user) throws RuntimeException {
        log.debug("Входящий запрос на создание пользователя {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Validated User user) throws RuntimeException {
        log.debug("Входящий запрос на редактирование пользователя{}", user);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws RuntimeException {
        log.debug("Входящий запрос на удаление пользователя с id = {}", id);
        userService.deleteUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) throws RuntimeException {
        log.debug("Входящий запрос на добавление в друзья пользователя с id = {} пользователю c id = {}",
                friendId, id);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) throws RuntimeException {
        log.debug("Входящий запрос на удаление из друзей пользователя с id = {} у пользователя c id = {}",
                friendId, id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) throws RuntimeException {
        log.debug("Входящий запрос на получение информации по пользователю с id = {}", id);
        return userService.get(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> allFriendsUser(@PathVariable Long id) throws RuntimeException {
        log.debug("Входящий запрос на получения списка друзей для пользователя с id = {}", id);
        return userService.getFriendsUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId)
            throws RuntimeException {
        log.debug("Входящий запрос на получения списка общих друзей для пользователей с id = {} и пользователя "
                + "с id = {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public Collection<Feed> getFeed(@PathVariable Long id)
            throws RuntimeException {
        log.debug("Входящий запрос на получение ленты событий для пользователя с id = {}", id);
        return userService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Long id) {
        return userService.getRecommendations(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
