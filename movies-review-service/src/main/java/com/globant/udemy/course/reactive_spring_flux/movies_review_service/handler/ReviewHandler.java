package com.globant.udemy.course.reactive_spring_flux.movies_review_service.handler;

import com.globant.udemy.course.reactive_spring_flux.movies_review_service.domain.Review;
import com.globant.udemy.course.reactive_spring_flux.movies_review_service.exception.ReviewDataException;
import com.globant.udemy.course.reactive_spring_flux.movies_review_service.exception.ReviewNotFoundException;
import com.globant.udemy.course.reactive_spring_flux.movies_review_service.repository.ReviewReactiveRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewHandler {

    private final ReviewReactiveRepository reviewReactiveRepository;
    private final Validator validator;
    Sinks.Many<Review> reviewSink = Sinks.many().replay().latest();

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .doOnNext(review -> reviewSink.tryEmitNext(review))
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        Set<ConstraintViolation<Review>> constraintViolation = validator.validate(review);
        log.info("constraintViolations: {}", constraintViolation);
        if (!constraintViolation.isEmpty()) {
            String errorMessage = constraintViolation.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }

    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        Optional<String> movieInfoId = request.queryParam("movieInfoId");
        return movieInfoId.map(s -> buildReviewResponse(reviewReactiveRepository.findReviewsByMovieInfoId(
                Long.valueOf(s)
        ))).orElseGet(() -> buildReviewResponse(reviewReactiveRepository.findAll()));
    }

    private Mono<ServerResponse> buildReviewResponse(Flux<Review> reviewsFlux) {
        return ServerResponse.ok()
                .body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        String reviewId = request.pathVariable("id");
        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the provided data")));
        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(ServerResponse.ok()::bodyValue)
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        String reviewId = request.pathVariable("id");
        return reviewReactiveRepository.findById(reviewId)
                .flatMap(reviewReactiveRepository::delete)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest request) {
        System.out.println("ACA");
        return ServerResponse.ok()
                .contentType(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .body(reviewSink.asFlux(), Review.class)
                .log();
    }
}
