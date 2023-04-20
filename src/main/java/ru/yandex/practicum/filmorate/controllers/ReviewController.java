package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        reviewService.createReview(review);
        log.info("[FT-8] Received request to POST /reviews with body: {}", review);
        return review;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("[FT-8] Received request to PUT /reviews with body: {}", review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        log.info("[FT-8] Received request to DELETE /reviews/{}", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Long reviewId) {
        log.info("[FT-8] Received request to GET /reviews/{}", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping()
    public List<Review> getReviewsByFilmId(@RequestParam(required = false) Long filmId,
                                           @RequestParam(defaultValue = "10", required = false) int count) {

        log.info("[FT-8] Received request to GET /reviews?filmId={}&count={}", filmId, count);
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeReview(@PathVariable Long reviewId,
                              @PathVariable Long userId) {
        log.info("[FT-8] Received request to PUT /reviews/{}/like/{}", reviewId, userId);
        reviewService.addLikeReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislikeReview(@PathVariable Long reviewId,
                                 @PathVariable Long userId) {
        log.info("[FT-8] Received request to PUT /reviews/{}/dislike/{}", reviewId, userId);
        reviewService.addDislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void deleteLikeReview(@PathVariable Long reviewId,
                                 @PathVariable Long userId) {
        log.info("[FT-8] Received request to DELETE /reviews/{}/like/{}", reviewId, userId);
        reviewService.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable Long reviewId,
                                    @PathVariable Long userId) {
        log.info("[FT-8] Received request to DELETE /reviews/{}/dislike/{}", reviewId, userId);
        reviewService.deleteDislikeReview(reviewId, userId);
    }
}
