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
        log.info("Received request to GET /users");
        return userService.getAll();
    }


    @PostMapping
    public User create(@RequestBody @Validated User user) throws RuntimeException {
        log.info("Received request to POST /users with body: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Validated User user) throws RuntimeException {
        log.info("Received request to UPDATE /users with body: {}", user);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws RuntimeException {
        log.info("[FT-4] Received request to DELETE /users/{}", id);
        userService.deleteUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) throws RuntimeException {
        log.info("Received request to PUT /users/{}/friends/{}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) throws RuntimeException {
        log.info("Received request to DELETE /users/{}/friends/{}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) throws RuntimeException {
        log.info("Received request to GET /users/{}", id);
        return userService.get(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> allFriendsUser(@PathVariable Long id) throws RuntimeException {
        log.info("Received request to GET /users/{}/friends", id);
        return userService.getFriendsUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId)
            throws RuntimeException {
        log.info("Received request to GET /users/{}/friends/common/{}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public Collection<Feed> getFeed(@PathVariable Long id)
            throws RuntimeException {
        log.info("[FT-1] Received request to GET /users/{}/feed", id);
        return userService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Long id) {
        log.info("[FT-7] Received request to GET /users/{}/recommendations", id);
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
