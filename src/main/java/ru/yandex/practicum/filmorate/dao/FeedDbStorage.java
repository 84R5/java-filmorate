package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Feed addFeed(Feed feed) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("USER_FEED").usingGeneratedKeyColumns("EVENT_ID");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENTITY_ID", feed.getEntityId());
        parameters.put("OPERATION", feed.getOperation().name());
        parameters.put("EVENT_TYPE", feed.getEventType().name());
        parameters.put("USER_ID", feed.getUserId());

        long epoch = System.currentTimeMillis();
        parameters.put("TIME_STAMP", epoch);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return getFeedById(key.longValue());
    }

    @Override
    public Feed getFeedById(Long id) {
        String sqlSelect = "SELECT * FROM USER_FEED WHERE EVENT_ID = ?";
        List<Feed> feeds = jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeFeed(rs), id);
        if (feeds.isEmpty()) {
            return null;
        }
        return feeds.get(0);
    }

    @Override
    public List<Feed> getFeed(Long userId) {
        String sqlSelect = "SELECT * FROM USER_FEED WHERE USER_ID = ?" +
                "ORDER BY TIME_STAMP ASC";

        List<Feed> feeds = jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeFeed(rs), userId);
        if (feeds.isEmpty()) {
            return Collections.emptyList();
        }
        return feeds;
    }

    private Feed makeFeed(ResultSet res) throws SQLException {
        Long eventId = res.getLong("EVENT_ID");
        Long entityId = res.getLong("ENTITY_ID");
        FeedOperation operation = FeedOperation.valueOf(res.getString("OPERATION"));
        FeedEventType eventType = FeedEventType.valueOf(res.getString("EVENT_TYPE"));
        Long userId = res.getLong("USER_ID");
        Long timestamp = res.getLong("TIME_STAMP");

        return Feed.builder().eventId(eventId).timestamp(timestamp).eventType(eventType).entityId(entityId).operation(operation).userId(userId).build();
    }
}
