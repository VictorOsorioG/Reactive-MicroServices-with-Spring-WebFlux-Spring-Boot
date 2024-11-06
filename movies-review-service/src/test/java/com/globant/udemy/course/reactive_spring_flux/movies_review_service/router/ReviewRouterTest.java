package com.globant.udemy.course.reactive_spring_flux.movies_review_service.router;

import com.globant.udemy.course.reactive_spring_flux.movies_review_service.domain.Review;
import com.globant.udemy.course.reactive_spring_flux.movies_review_service.exception.GlobalExceptionHandler;
import com.globant.udemy.course.reactive_spring_flux.movies_review_service.handler.ReviewHandler;
import com.globant.udemy.course.reactive_spring_flux.movies_review_service.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@AutoConfigureWebTestClient
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalExceptionHandler.class})
class ReviewRouterTest {

    private final String BASE_URL = "/api/v1/movies-review/reviews";

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    Review movieReview = Review.builder()
            .movieInfoId(1L)
            .comment("Great")
            .rating(8.0)
            .build();
    Review awesomeReview = Review.builder()
            .id("qwerty")
            .movieInfoId(1L)
            .comment("Awesome")
            .rating(9.0)
            .build();

    List<Review> reviews;

    @BeforeEach
    void setUp() {
        reviews = List.of(
                awesomeReview,
                Review.builder()
                        .movieInfoId(1L)
                        .comment("Excellent")
                        .rating(10.0)
                        .build(),
                Review.builder()
                        .movieInfoId(2L)
                        .comment("Garbage")
                        .rating(1.0)
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addReview() {
        Mockito.when(reviewReactiveRepository.save(movieReview))
                .thenAnswer((Answer<Mono<Review>>) invocationOnMock -> {
                    Review review = invocationOnMock.getArgument(0);
                    review.setId("1");
                    return Mono.just(review);
                });
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewExchangeResult -> {
                    Review savedReview = reviewExchangeResult.getResponseBody();
                    assertNotNull(savedReview);
                    assertNotNull(savedReview.getId());
                    assertEquals("1", savedReview.getId());
                });
    }

    @Test
    void getReviews() {
        Mockito.when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(reviews));
        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {
        Review awesomeReviewUpdated = Review.builder()
                .id("qwerty")
                .movieInfoId(1L)
                .comment("Great")
                .rating(8.0)
                .build();
        Mockito.when(reviewReactiveRepository.findById("qwerty"))
                .thenReturn(Mono.just(awesomeReview));
        Mockito.when(reviewReactiveRepository.save(Mockito.any(Review.class)))
                .thenReturn(Mono.just(awesomeReviewUpdated));
        webTestClient.put()
                .uri(BASE_URL + "/{id}", "qwerty")
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").isEqualTo("qwerty")
                .jsonPath("$.comment").isEqualTo(movieReview.getComment());
    }

    @Test
    void deleteReview() {
        Mockito.when(reviewReactiveRepository.findById("qwerty"))
                .thenReturn(Mono.just(awesomeReview));
        Mockito.when(reviewReactiveRepository.delete(Mockito.any(Review.class)))
                        .thenReturn(Mono.empty());
        webTestClient.delete()
                .uri(BASE_URL + "/{id}", "qwerty")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void addReviewValidation() {
        Review invalidMovieReview =  Review.builder()
                .movieInfoId(null)
                .comment("Great")
                .rating(-8.0)
                .build();
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(invalidMovieReview)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Movie info Id must no be null,Rating must be a non-negative value");
    }

    @Test
    void updateReviewInvalidReviewId() {
        Mockito.when(reviewReactiveRepository.findById("0"))
                .thenReturn(Mono.empty());
        webTestClient.put()
                .uri(BASE_URL + "/{id}", "0")
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

}