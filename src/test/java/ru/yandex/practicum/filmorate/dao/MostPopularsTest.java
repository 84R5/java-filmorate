package ru.yandex.practicum.filmorate.dao;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MostPopularsTest {
    private final FilmDbStorage filmDbStorage;


    private final Film film1 = Film.builder()
            .name("Покемон фильм первый: Мьюту против Мью")
            .description("Создан первый искусственный покемон")
            .releaseDate(LocalDate.of(1998, 7, 18))
            .duration(75L)
            .mpa(MPA.builder().id(1L).build())
            .genres(Arrays.asList(new Genre(3L, "Мультфильм")))
            .rate(2)
            .build();

    private final Film film2 = Film.builder()
            .name("Покемон 2000")
            .description("Древние предсказания сбываются, когда покемоны Артикуно, Запдос и Молтрес")
            .releaseDate(LocalDate.of(1999, 7, 17))
            .duration(84L)
            .mpa(MPA.builder().id(1L).build())
            .genres(Arrays.asList(new Genre(3L, "Мультфильм")))
            .rate(1)
            .build();

    private final Film film3 = Film.builder()
            .name("Покемон 3")
            .description("На Гринфилд наложено заклятие, и теперь Эш, Пикачу и их друзья должны найти способ восстановить некогда прекрасный город")
            .releaseDate(LocalDate.of(2000, 7, 17))
            .duration(93L)
            .mpa(MPA.builder().id(1L).build())
            .genres(Arrays.asList(new Genre(3L, "Мультфильм")))
            .rate(3)
            .build();

    private final Film film4 = Film.builder()
            .name("Дэдпул ")
            .description("Уэйд Уилсон — наёмник.  Будучи побочным продуктом программы вооружённых сил " +
                    "под названием «Оружие X», Уилсон приобрёл невероятную силу, проворство и способность к исцелению. ")
            .releaseDate(LocalDate.of(2016, 02, 11))
            .duration(108L)
            .mpa(MPA.builder().id(4L).build())
            .genres(Arrays.asList(new Genre(6L, "Боевик")))
            .rate(6)
            .build();

    @BeforeEach
    public void addFilms() {
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        filmDbStorage.create(film3);
        filmDbStorage.create(film4);
    }

    @Test
    public void topByGenre() {
        List<Film> list = filmDbStorage.getPopular(10, 3, null);
        assertEquals(3, filmDbStorage.getPopular(10, 3, null).size());
        assertEquals(filmDbStorage.findFilmById(film1.getId()), list.get(0));
        assertEquals(filmDbStorage.findFilmById(film2.getId()), list.get(1));
        assertEquals(filmDbStorage.findFilmById(film3.getId()), list.get(2));

    }

    @Test
    public void topByYear() {
        List<Film> list = filmDbStorage.getPopular(10, null, 2000);
        assertEquals(1, filmDbStorage.getPopular(10, null, 2000).size());
        assertEquals(filmDbStorage.findFilmById(film3.getId()), list.get(0));
    }

    @Test
    public void topAll() {
        List<Film> list = filmDbStorage.getPopular(10, null, null);
        assertEquals(4, filmDbStorage.getPopular(10, null, null).size());
        assertEquals(filmDbStorage.findFilmById(film1.getId()), list.get(0));
        assertEquals(filmDbStorage.findFilmById(film2.getId()), list.get(1));
        assertEquals(filmDbStorage.findFilmById(film3.getId()), list.get(2));
        assertEquals(filmDbStorage.findFilmById(film4.getId()), list.get(3));
    }

}
