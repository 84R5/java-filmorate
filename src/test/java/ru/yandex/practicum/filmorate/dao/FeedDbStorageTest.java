package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedDbStorageTest {
    private final UserDbStorage userStorage;
    private final FeedDbStorage feedDbStorage;
    User user;

    @BeforeEach
    void init() {
        User tempUser = User.builder().login("Mike").email("mike@mail.ru").birthday(LocalDate.of(2000, 12, 27)).build();
        user = userStorage.create(tempUser);
    }

    @Test
    public void addFeed_RecordedEventMustBeOfTheType_FRIEND() {
        Feed feed = Feed.builder().userId(user.getId()).entityId(1L).operation(FeedOperation.ADD).eventType(FeedEventType.FRIEND).build();

        Feed feedInStorage = feedDbStorage.addFeed(feed);
        assertThat("FRIEND", Matchers.is(feedInStorage.getEventType().name()));
    }

    @Test
    public void getFeed_SizeOfTheEventListShouldBeGreaterThan_2() {
        Feed feed1 = Feed.builder().userId(user.getId()).entityId(1L).operation(FeedOperation.ADD).eventType(FeedEventType.FRIEND).build();
        Feed feed2 = Feed.builder().userId(user.getId()).entityId(1L).operation(FeedOperation.ADD).eventType(FeedEventType.LIKE).build();

        Feed feed3 = Feed.builder().userId(user.getId()).entityId(1L).operation(FeedOperation.ADD).eventType(FeedEventType.FRIEND).build();

        feedDbStorage.addFeed(feed1);
        feedDbStorage.addFeed(feed2);
        feedDbStorage.addFeed(feed3);
        assertThat(feedDbStorage.getFeed(user.getId()).size(), Matchers.greaterThan(2));
    }

    @Test
    public void getFeed_LastEventShouldBeTheLastInList() {
        Feed feed1 = Feed.builder().userId(user.getId()).entityId(1L).operation(FeedOperation.ADD).eventType(FeedEventType.FRIEND).build();
        Feed feed2 = Feed.builder().userId(user.getId()).entityId(1L).operation(FeedOperation.ADD).eventType(FeedEventType.LIKE).build();

        Feed feed3 = Feed.builder().userId(user.getId()).entityId(1L).operation(FeedOperation.ADD).eventType(FeedEventType.FRIEND).build();

        feedDbStorage.addFeed(feed1);
        feedDbStorage.addFeed(feed2);
        feedDbStorage.addFeed(feed3);
        assertThat(feedDbStorage.getFeed(user.getId()).get(1).getEventId(), Matchers.greaterThan(feedDbStorage.getFeed(user.getId()).get(0).getEventId()));
    }

    @Test
    public void getFeed_ShouldBeAnEmptyList() {
        assertThat(feedDbStorage.getFeed(999L).size(), Matchers.is(0));
    }
}
