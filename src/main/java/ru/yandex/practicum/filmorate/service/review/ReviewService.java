package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    private final FeedStorage feedStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public Review createReview(Review review) {
        if (review.getReviewId() != null) {
            throw new ValidationException("Ревью уже существует");
        } else if (filmDbStorage.findFilmById(review.getFilmId()) == null) {
            throw new NotFoundException("Такой фильм не существует");
        } else if (userDbStorage.findUserById(review.getUserId()) == null) {
            throw new NotFoundException("Такой пользователь не существует");
        } else {
            Review reviewInStorage = reviewDbStorage.createReview(review);
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.ADD).eventType(FeedEventType.REVIEW).entityId(reviewInStorage.getReviewId()).userId(review.getUserId()).build());
            return reviewInStorage;
        }
    }

    public Review updateReview(Review review) {
        if (reviewDbStorage.getReviewById(review.getReviewId()) == null) {
            throw new ValidationException("Ревью не существует.");
        } else {
            Review reviewInStorage = reviewDbStorage.updateReview(review);
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.UPDATE).eventType(FeedEventType.REVIEW).entityId(reviewInStorage.getReviewId()).userId(reviewInStorage.getUserId()).build());
            return reviewInStorage;
        }

    }

    public void deleteReview(Long reviewId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            throw new ValidationException("Ревью не существует");
        } else {
            feedStorage.addFeed(Feed.builder().operation(FeedOperation.REMOVE).eventType(FeedEventType.REVIEW).entityId(reviewId).userId(getReviewById(reviewId).getUserId()).build());
            reviewDbStorage.deleteReview(reviewId);
        }

    }

    public Review getReviewById(Long reviewId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            throw new NotFoundException("Ревью не существует");
        } else {
            return reviewDbStorage.getReviewById(reviewId);
        }
    }

    public List<Review> getAllReviews() {
        return reviewDbStorage.getAllReviews();
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        return reviewDbStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            throw new ValidationException("Ревью не существует");
        } else if (userDbStorage.findUserById(userId) == null) {
            throw new ValidationException("Пользователь не найден.");
        } else {
            reviewLikeDbStorage.addLike(reviewId, userId);
        }

    }

    public void addDislikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            throw new ValidationException("Ревью не существует");
        } else if (userDbStorage.findUserById(userId) == null) {
            throw new ValidationException("Пользователь не найден.");
        } else {
            reviewLikeDbStorage.addDislike(reviewId, userId);
        }

    }

    public void deleteLikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            throw new ValidationException("Ревью не существует");
        } else if (userDbStorage.findUserById(userId) == null) {
            throw new ValidationException("Пользователь не найден.");
        } else {
            reviewLikeDbStorage.deleteLike(reviewId, userId);
        }

    }

    public void deleteDislikeReview(Long reviewId, Long userId) {
        if (reviewDbStorage.getReviewById(reviewId) == null) {
            throw new ValidationException("Ревью не существует");
        } else {
            reviewLikeDbStorage.deleteDislike(reviewId, userId);
        }
    }
}
