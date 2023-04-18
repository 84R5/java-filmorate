package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void checkCreatedUserFindUserById() {
        User user = User.builder()
                .email("")
                .name("UserName")
                .birthday(LocalDate.of(1990, 6, 9))
                .login("UserLogin")
                .build();

        User userInStorage = userDbStorage.create(user);

        User getUser = userDbStorage.findUserById(userInStorage.getId());
        assertThat(getUser.getName(), is(equalTo(userInStorage.getName())));
    }

    @Test
    public void checkUpdatedUserFindUserById() {
        User user = User.builder()
                .email("wwww@ya.ru")
                .name("UserName")
                .birthday(LocalDate.of(1990, 6, 9))
                .login("UserLogin")
                .build();
        userDbStorage.create(user);

        userDbStorage.update(User.builder()
                .id(1L)
                .email("google@yandex.ru")
                .name("UserNameEdit")
                .birthday(LocalDate.of(1988, 3, 4))
                .login("UserLoginEdit")
                .build());

        User userToCompare = User.builder()
                .id(1L)
                .email("google@yandex.ru")
                .name("UserNameEdit")
                .birthday(LocalDate.of(1988, 3, 4))
                .login("UserLoginEdit")
                .friends(new HashSet<>())
                .build();

        User getUser = userDbStorage.findUserById(1L);
        assertThat(getUser, is(equalTo(userToCompare)));
    }

    @Test
    public void checkUsersGetAllUsers() {
        User user1 = User.builder()
                .email("qwe@mail.com")
                .name("UserName1")
                .birthday(LocalDate.of(1990, 6, 9))
                .login("UserLogin")
                .build();
        userDbStorage.create(user1);

        User user2 = User.builder()
                .email("asd@mail.com")
                .name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2))
                .login("UserLogin2")
                .build();
        userDbStorage.create(user2);

        List<User> listToCompare = new ArrayList<>();

        User user1ToCompare = User.builder()
                .id(1L).email("qwe@mail.com")
                .name("UserName1")
                .birthday(LocalDate.of(1990, 6, 9))
                .login("UserLogin")
                .build();

        User user2ToCompare = User.builder()
                .id(2L).email("asd@mail.com")
                .name("UserName2")
                .birthday(LocalDate.of(1986, 1, 2))
                .login("UserLogin2")
                .build();

        listToCompare.add(user1ToCompare);
        listToCompare.add(user2ToCompare);
        assertThat(userDbStorage.getUsers(), is(equalTo(listToCompare)));
    }

    @Test
    public void checkCreatedFilmFindFilmById() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(120L)
                .mpa(MPA.builder()
                        .id(3L)
                        .build())
                .directors(new ArrayList<>())
                .build();

        Film filmToCompare = Film.builder()
                .id(1L).name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(120L)
                .genres(new ArrayList<>())
                .mpa(MPA.builder()
                        .id(3L)
                        .name("PG-13")
                        .build())
                .directors(new ArrayList<>())
                .build();

        filmDbStorage.create(film);
        assertThat(filmDbStorage.findFilmById(1L), is(equalTo(filmToCompare)));
    }

    @Test
    public void checkUpdatedFilmFindFilmById() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(120L)
                .mpa(MPA.builder()
                        .id(3L)
                        .build())
                .build();
        filmDbStorage.create(film);

        filmDbStorage.updateFilm(Film.builder()
                .id(1L)
                .name("NameEdit")
                .description("DescriptionEdit")
                .releaseDate(LocalDate.of(2003, 5, 12))
                .duration(118L)
                .mpa(MPA.builder()
                        .id(1L)
                        .build())
                .directors(new ArrayList<>())
                .build());

        Film filmToCompare = Film.builder()
                .id(1L).name("NameEdit")
                .description("DescriptionEdit")
                .releaseDate(LocalDate.of(2003, 5, 12))
                .duration(118L)
                .genres(new ArrayList<>())
                .mpa(MPA.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .directors(new ArrayList<>())
                .build();
        assertThat(filmDbStorage.findFilmById(1L), is(equalTo(filmToCompare)));
    }

    @Test
    public void checkFilmsGetAllFilms() {
        Film film1 = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(120L)
                .mpa(MPA.builder().id(3L).build())
                .build();
        filmDbStorage.create(film1);

        Film film2 = Film.builder()
                .name("Name2")
                .description("Description2")
                .releaseDate(LocalDate.of(2003, 2, 7))
                .duration(112L)
                .mpa(MPA.builder()
                        .id(2L)
                        .build())
                .build();
        filmDbStorage.create(film2);

        List<Film> listToCompare = new ArrayList<>();

        Film film1ToCompare = Film.builder()
                .id(1L).name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(120L)
                .genres(new ArrayList<>())
                .mpa(MPA.builder()
                        .id(3L)
                        .name("PG-13")
                        .build())
                .directors(new ArrayList<>())
                .build();

        Film film2ToCompare = Film.builder()
                .id(2L)
                .name("Name2")
                .description("Description2")
                .releaseDate(LocalDate.of(2003, 2, 7))
                .duration(112L)
                .genres(new ArrayList<>())
                .mpa(MPA.builder()
                        .id(2L)
                        .name("PG")
                        .build())
                .directors(new ArrayList<>())
                .build();

        listToCompare.add(film1ToCompare);
        listToCompare.add(film2ToCompare);
        assertThat(filmDbStorage.getFilms(), is(equalTo(listToCompare)));
    }
}