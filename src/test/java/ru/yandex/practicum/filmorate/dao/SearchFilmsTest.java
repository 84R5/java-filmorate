package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SearchFilmsTest {

    private final FilmDbStorage filmDbStorage;

    private final DirectorDbStorage directorDbStorage;

    @Test
    public void checkFilmsGetSearchFilms() {

        Director director1 = Director.builder().name("Director update").build();
        directorDbStorage.createDirector(director1);

        Film film1 = Film.builder()
                .name("New Film")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27))
                .duration(120L)
                .mpa(MPA.builder().id(3L).build())
                .build();
        film1.setDirectors(List.of(directorDbStorage.getDirector(1L)));
        filmDbStorage.create(film1);

        Film film2 = Film.builder()
                .name("New Films2 update")
                .description("Description2")
                .releaseDate(LocalDate.of(2003, 2, 7))
                .duration(112L)
                .mpa(MPA.builder()
                        .id(2L)
                        .build())
                .build();
        filmDbStorage.create(film2);


        Director director2 = Director.builder().name("Update Director").build();
        directorDbStorage.createDirector(director2);
        Film film3 = Film.builder()
                .name("New Films From Director")
                .description("Description2")
                .releaseDate(LocalDate.of(2003, 2, 7))
                .duration(112L)
                .mpa(MPA.builder()
                        .id(2L)
                        .build())
                .build();
        film3.setDirectors(List.of(directorDbStorage.getDirector(2L)));
        filmDbStorage.create(film3);

        Director director3 = Director.builder().name("Director unknown").build();
        directorDbStorage.createDirector(director3);
        Film film4 = Film.builder()
                .name("Parry Hotter")
                .description("A boy who learned program Java")
                .releaseDate(LocalDate.of(2020, 1, 27))
                .duration(120L)
                .mpa(MPA.builder().id(3L).build())
                .build();
        film4.setDirectors(List.of(directorDbStorage.getDirector(3L)));
        filmDbStorage.create(film4);

        List<Film> searchByTitle = new ArrayList<>();
        searchByTitle.add(filmDbStorage.findFilmById(2L));

        List<Film> searchByDirector = new ArrayList<>();
        searchByDirector.add(filmDbStorage.findFilmById(1L));
        searchByDirector.add(filmDbStorage.findFilmById(3L));

        List<Film> searchByDirectorAndTitle = new ArrayList<>();
        searchByDirectorAndTitle.add(filmDbStorage.findFilmById(1L));
        searchByDirectorAndTitle.add(filmDbStorage.findFilmById(2L));
        searchByDirectorAndTitle.add(filmDbStorage.findFilmById(3L));

        assertThat(filmDbStorage.getSearchFilms("UpDaTe", "title", 10), is(equalTo(searchByTitle)));
        assertThat(filmDbStorage.getSearchFilms("UpDaTe", "director", 10), is(equalTo(searchByDirector)));
        assertThat(filmDbStorage.getSearchFilms("UpDaTe", "title,director", 10),
                is(equalTo(searchByDirectorAndTitle)));
    }
}
