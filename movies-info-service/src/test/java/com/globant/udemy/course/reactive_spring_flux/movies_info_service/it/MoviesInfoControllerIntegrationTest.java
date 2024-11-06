package com.globant.udemy.course.reactive_spring_flux.movies_info_service.it;

import com.globant.udemy.course.reactive_spring_flux.movies_info_service.domain.MovieInfo;
import com.globant.udemy.course.reactive_spring_flux.movies_info_service.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoviesInfoControllerIntegrationTest {

    private final String BASE_URL = "/api/v1/movies-info";
    private final String prisonersMovieInfoId = "qwerty123456";

    @Autowired
    MovieInfoRepository movieInfoRepository;
    @Autowired
    WebTestClient webTestClient;

    MovieInfo prisonersMovieInfo;
    MovieInfo oppenheimerMovieInfo;
    MovieInfo batmanMovieInfo;

    @BeforeEach
    void setUp() {
        prisonersMovieInfo = MovieInfo.builder()
                .id(prisonersMovieInfoId)
                .name("Prisoners")
                .cast(
                        List.of("Hugh Jackman", "Jake Gyllenhaal")
                )
                .year(2013)
                .releaseDate(LocalDate.parse("2013-09-20"))
                .build();
        oppenheimerMovieInfo = MovieInfo.builder()
                .id("1234567")
                .name("Oppenheimer")
                .cast(
                        List.of("Cillian Murphy", "Emily Blunt")
                )
                .year(2023)
                .releaseDate(LocalDate.parse("2023-07-21"))
                .build();
        batmanMovieInfo = MovieInfo.builder()
                .name("Batman Begins")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();
        movieInfoRepository
                .saveAll(List.of(prisonersMovieInfo, oppenheimerMovieInfo))
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(batmanMovieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo savedMovie = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovie);
                    assertNotNull(savedMovie.getId());
                });
    }

    @Test
    void getMoviesInfo() {
        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    void getMovieInfo() {
        webTestClient.get()
                .uri(BASE_URL + "/{id}", prisonersMovieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                    assertEquals(prisonersMovieInfo, movieInfo);
                });
    }

    @Test
    void updateMovieInfo() {
        webTestClient.put()
                .uri(BASE_URL + "/{id}", prisonersMovieInfoId)
                .bodyValue(batmanMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo(batmanMovieInfo.getName());
    }

    @Test
    void deleteMovieInfo() {
        webTestClient.delete()
                .uri(BASE_URL + "/{id}", prisonersMovieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void updateMovieInfoNotFound() {
        webTestClient.put()
                .uri(BASE_URL + "/{id}", "0")
                .bodyValue(batmanMovieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getMovieInfoNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/{id}", "0")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getMoviesInfoByYear() {
        webTestClient.get()
                .uri(UriComponentsBuilder.fromUriString(BASE_URL)
                        .queryParam("name", "Prisoners")
                        .toUriString())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMoviesInfoByName() {
        webTestClient.get()
                .uri(UriComponentsBuilder.fromUriString(BASE_URL)
                        .queryParam("year", 2013)
                        .toUriString())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMoviesInfoStream() {
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(batmanMovieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo savedMovie = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovie);
                    assertNotNull(savedMovie.getId());
                });

        Flux<MovieInfo> movieInfoFlux = webTestClient.get()
                .uri(BASE_URL+ "/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        StepVerifier.create(movieInfoFlux)
                .assertNext(movieInfo -> assertNotNull(movieInfo.getId()))
                .thenCancel()
                .verify();
    }
}
