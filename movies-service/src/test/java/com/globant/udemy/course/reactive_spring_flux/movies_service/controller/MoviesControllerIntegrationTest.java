package com.globant.udemy.course.reactive_spring_flux.movies_service.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.globant.udemy.course.reactive_spring_flux.movies_service.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        properties = {
                "restClient.moviesInfoUrl=http://localhost:8084/api/v1/movies-info",
                "restClient.moviesReviewUrl=http://localhost:8084/api/v1/movies-review/reviews"
        }
)
class MoviesControllerIntegrationTest {

    private final String BASE_URL = "/api/v1/movies";
    private final String BASE_MOVIE_INFO_URL = "/api/v1/movies-info";
    private final String BASE_REVIEW_URL = "/api/v1/movies-review/reviews";

    @Autowired
    WebTestClient webTestClient;

    @Test
    void retrieveMovieById() {
        String movieId = "1";
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlEqualTo(BASE_MOVIE_INFO_URL + "/" + movieId)
                        )
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("movieInfo.json")
                        )
        );
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlPathEqualTo(BASE_REVIEW_URL)
                        )
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("reviews.json")
                        )
        );
        webTestClient
                .get()
                .uri(BASE_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    Movie movieResponse = movieEntityExchangeResult.getResponseBody();
                    assertNotNull(movieResponse);
                    assertNotNull(movieResponse.getMovieInfo());
                    assertEquals(2, movieResponse.getReviewList().size());
                    assertEquals("Batman Begins", movieResponse.getMovieInfo().getName());
                });
    }

    @Test
    void retrieveMovieByWrongId() {
        String movieId = "0";
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlEqualTo(BASE_MOVIE_INFO_URL + "/" + movieId)
                        )
                        .willReturn(WireMock.aResponse()
                                .withStatus(404)
                        )
        );
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlPathEqualTo(BASE_REVIEW_URL)
                        )
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("reviews.json")
                        )
        );
        webTestClient
                .get()
                .uri(BASE_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    void retrieveMovieByIdNoReviews() {
        String movieId = "1";
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlEqualTo(BASE_MOVIE_INFO_URL + "/" + movieId)
                        )
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("movieInfo.json")
                        )
        );
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlPathEqualTo(BASE_REVIEW_URL)
                        )
                        .willReturn(WireMock.aResponse()
                                .withStatus(404)
                        )
        );
        webTestClient
                .get()
                .uri(BASE_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    Movie movieResponse = movieEntityExchangeResult.getResponseBody();
                    assertNotNull(movieResponse);
                    assertNotNull(movieResponse.getMovieInfo());
                    assertEquals(0, movieResponse.getReviewList().size());
                    assertEquals("Batman Begins", movieResponse.getMovieInfo().getName());
                });
    }

    @Test
    void retrieveMovieByIdServicesDown() {
        String movieId = "1";
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlEqualTo(BASE_MOVIE_INFO_URL + "/" + movieId)
                        )
                        .willReturn(WireMock.aResponse()
                                .withStatus(500)
                                .withBody("Movie Info service unavailable")
                        )
        );
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlPathEqualTo(BASE_REVIEW_URL)
                        )
                        .willReturn(WireMock.aResponse()
                                .withStatus(500)
                                .withBody("Movie Info service unavailable")
                        )
        );
        webTestClient
                .get()
                .uri(BASE_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    String message = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(message);
                    assert  message.contains("Server is down");
                });
    }

    @Test
    void retrieveMovieByIdServicesDownRetry() {
        String movieId = "1";
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlEqualTo(BASE_MOVIE_INFO_URL + "/" + movieId)
                        )
                        .willReturn(WireMock.aResponse()
                                .withStatus(500)
                                .withBody("Movie Info service unavailable")
                        )
        );
        WireMock.stubFor(
                WireMock.get(
                                WireMock.urlPathEqualTo(BASE_REVIEW_URL)
                        )
                        .willReturn(WireMock.aResponse()
                                .withStatus(500)
                                .withBody("Movie Info service unavailable")
                        )
        );
        webTestClient
                .get()
                .uri(BASE_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    String message = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(message);
                    assert  message.contains("Server is down");
                });

        WireMock.verify(4,
                WireMock.getRequestedFor(WireMock.urlPathEqualTo(BASE_MOVIE_INFO_URL + "/" + movieId)));
    }
}