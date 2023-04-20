package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Qualifier
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Review createReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Long reviewId = simpleJdbcInsert.executeAndReturnKey(toMap(review)).longValue();
        review.setReviewId(reviewId);

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews " +
                "SET content =?, is_positive =? " +
                "WHERE review_id =?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(Long reviewId) {
        String sql = "DELETE " +
                "FROM reviews " +
                "WHERE review_id =?";

        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        String sql = "SELECT * " +
                "FROM reviews " +
                "WHERE review_id =?";

        List<Review> review = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), reviewId);
        if (review.isEmpty()) {
            return null;
        }
        return review.get(0);
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * " +
                "FROM reviews " +
                "ORDER BY useful DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        String sql = "SELECT * " +
                "FROM reviews " +
                "WHERE film_id =? " +
                "ORDER BY review_id " +
                "LIMIT ?";

        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);
        reviews.sort(Comparator.comparing(Review::getUseful).reversed());
        return reviews;
    }


    private Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("CONTENT", review.getContent());
        values.put("USER_ID", review.getUserId());
        values.put("FILM_ID", review.getFilmId());
        values.put("USEFUL", 0);
        values.put("IS_POSITIVE", review.getIsPositive());
        return values;
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("review_id"));
        review.setContent(rs.getString("content"));
        review.setUserId(rs.getLong("user_id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUseful(rs.getInt("useful"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        return review;
    }
}

