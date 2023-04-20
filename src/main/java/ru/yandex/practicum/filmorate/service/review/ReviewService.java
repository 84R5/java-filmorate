package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewLikeDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    private final FeedStorage feedStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public Review createReview(Review review) {
        if (review.getReviewId() != null) {
            log.warn("Review already exists");
            throw new ValidationException("Ревью уже существует");
        } else if (filmDbStorage.findFilmById(review.getFilmId()) == null) {
            log.warn("Film not found");
            throw new NotFoundException("Film not found");
        } else if (userDbStorage.findUserById(review.getUserId()) == null) {
            log.warn("User not found");
            throw new NotFoundException("User not found");
        } else {
            Review reviewInStorage = reviewDbStorage.createReview(review);
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.ADD).eventType(FeedEventType.REVIEW).entityId(reviewInStorage.getReviewId()).userId(review.getUserId()).build());
            return reviewInStorage;
        }
    }

    public Review updateReview(Review review) {
        if (reviewDbStorage.getReviewById(review.getReviewId()) == null) {
            log.warn("Review not found");
            throw new ValidationException("Review not found");
        } else {
            Review reviewInStorage = reviewDbStorage.updateReview(review);
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.UPDATE).eventType(FeedEventType.REVIEW).entityId(reviewInStorage.getReviewId()).userId(reviewInStorage.getUserId()).build());
            return reviewInStorage;
        }

    }

    public void deleteReview(Long reviewId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            log.warn("Review not found");
            throw new ValidationException("Review not found");
        } else {
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.REMOVE).eventType(FeedEventType.REVIEW).entityId(reviewId).userId(getReviewById(reviewId).getUserId()).build());
            reviewDbStorage.deleteReview(reviewId);
        }

    }

    public Review getReviewById(Long reviewId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            log.warn("Review not found");
            throw new NotFoundException("Review not found");
        } else {
            return reviewDbStorage.getReviewById(reviewId);
        }
    }

    public List<Review> getAllReviews() {
        return reviewDbStorage.getAllReviews();
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        if (filmId == null) {
            return reviewDbStorage.getAllReviews();
        }
        if (count < 0) {
            log.warn("Number of required reviews cannot be negative");
            throw new IllegalArgumentException("Number of required reviews cannot be negative");
        }
        return reviewDbStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            log.warn("Review not found");
            throw new ValidationException("Review not found");
        } else if (userDbStorage.findUserById(userId) == null) {
            log.warn("User not found");
            throw new ValidationException("User not found");
        } else {
            reviewLikeDbStorage.addLike(reviewId, userId);
        }

    }

    public void addDislikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            log.warn("Review not found");
            throw new ValidationException("Review not found");
        } else if (userDbStorage.findUserById(userId) == null) {
            log.warn("User not found");
            throw new ValidationException("User not found");
        } else {
            reviewLikeDbStorage.addDislike(reviewId, userId);
        }

    }

    public void deleteLikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            log.warn("Review not found");
            throw new ValidationException("Review not found");
        } else if (userDbStorage.findUserById(userId) == null) {
            log.warn("User not found");
            throw new ValidationException("User not found");
        } else {
            reviewLikeDbStorage.deleteLike(reviewId, userId);
        }

    }

    public void deleteDislikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            log.warn("Review not found");
            throw new ValidationException("Review not found");
        } else {
            reviewLikeDbStorage.deleteDislike(reviewId, userId);
        }
    }
}
