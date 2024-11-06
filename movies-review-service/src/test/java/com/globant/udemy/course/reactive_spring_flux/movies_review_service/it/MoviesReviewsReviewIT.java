package com.globant.udemy.course.reactive_spring_flux.movies_review_service.it;

import com.globant.udemy.course.reactive_spring_flux.movies_review_service.domain.Review;
import com.globant.udemy.course.reactive_spring_flux.movies_review_service.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoviesReviewsReviewIT {

    private final String BASE_URL = "/api/v1/movies-review/reviews";

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    Review movieReview =  Review.builder()
            .movieInfoId(1L)
            .comment("Great")
            .rating(8.0)
            .build();

    @BeforeEach
    void setUp() {
        reviewReactiveRepository.saveAll(List.of(
                        Review.builder()
                                .id("qwerty")
                                .movieInfoId(1L)
                                .comment("Awesome")
                                .rating(9.0)
                                .build(),
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
                ))
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll()
                .block();
    }

    @Test
    void addReview() {
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
                });
    }

    @Test
    void getReviews() {
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
        webTestClient.put()
                .uri(BASE_URL + "/{id}", "qwerty")
                .bodyValue(movieReview)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.comment").isEqualTo(movieReview.getComment());
    }

    @Test
    void deleteReview() {
        webTestClient.delete()
                .uri(BASE_URL + "/{id}", "qwerty")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getReviewsByMovieInfoId() {
        webTestClient.get()
                .uri(UriComponentsBuilder.fromUriString(BASE_URL)
                        .queryParam("movieInfoId", "1")
                        .toUriString())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);
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
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewExchangeResult -> {
                    Review savedReview = reviewExchangeResult.getResponseBody();
                    assertNotNull(savedReview);
                    assertNotNull(savedReview.getId());
                });
    }
}
