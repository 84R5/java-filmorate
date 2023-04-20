package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    Feed addFeed(Feed feed);

    List<Feed> getFeed(Long userId);

    Feed getFeedById(Long id);
}
