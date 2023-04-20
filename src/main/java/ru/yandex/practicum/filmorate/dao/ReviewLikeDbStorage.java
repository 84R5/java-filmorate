package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;


    @Override
    public void addLike(Long reviewId, Long userId) {

        ReviewLike like = ReviewLike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .like(true)
                .build();

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review_likes");
        insert.execute(toMap(like));
        updateUseful(reviewId, true);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {

        String sql = "DELETE FROM review_likes " +
                "WHERE review_id =? AND user_id =?";

        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId, false);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {

        ReviewLike like = ReviewLike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .like(false)
                .build();

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review_likes");
        insert.execute(toMap(like));
        updateUseful(reviewId, false);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        if (userDbStorage.findUserById(userId) == null) {
            throw new ValidationException("Пользователь не найден.");
        }

        String sql = "DELETE FROM review_likes " +
                "WHERE review_id =? AND user_id =?";

        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId, true);
    }

    public void updateUseful(Long reviewId, boolean like) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            throw new ValidationException("Отзыв не найден.");
        }

        Integer useful = reviewDbStorage.getReviewById(reviewId).getUseful();
        if (like) {
            useful += 1;
        } else {
            useful -= 1;
        }

        String sql = "UPDATE reviews " +
                "SET useful = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, useful, reviewId);
    }

    private Map<String, Object> toMap(ReviewLike reviewLike) {
        Map<String, Object> map = new HashMap<>();
        map.put("review_id", reviewLike.getReviewId());
        map.put("user_id", reviewLike.getUserId());
        map.put("like", reviewLike.getLike());
        return map;
    }
}
