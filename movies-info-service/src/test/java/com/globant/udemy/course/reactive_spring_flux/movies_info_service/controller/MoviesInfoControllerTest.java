package com.globant.udemy.course.reactive_spring_flux.movies_info_service.controller;

import com.globant.udemy.course.reactive_spring_flux.movies_info_service.domain.MovieInfo;
import com.globant.udemy.course.reactive_spring_flux.movies_info_service.service.MovieInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@WebFluxTest(MoviesInfoController.class)
class MoviesInfoControllerTest {

    private final String BASE_URL = "/api/v1/movies-info";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    MovieInfoService movieInfoService;

    private final String prisonersMovieInfoId = "qwerty123456";
    MovieInfo prisonersMovieInfo;
    MovieInfo oppenheimerMovieInfo;
    MovieInfo batmanMovieInfo;
    Flux<MovieInfo> movieInfoFlux;

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
        movieInfoFlux = Flux.fromIterable(List.of(prisonersMovieInfo, oppenheimerMovieInfo));
    }

    @Test
    void addMovieInfo() {
        Mockito.when(movieInfoService.addMovieInfo(batmanMovieInfo))
                        .thenAnswer((Answer<Mono<MovieInfo>>) invocationOnMock -> {
                            MovieInfo movieInfoSaved = invocationOnMock.getArgument(0);
                            movieInfoSaved.setId("1");
                            return Mono.just(movieInfoSaved);
                        });
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(batmanMovieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    void getMoviesInfo() {
        Mockito.when(movieInfoService.getMoviesInfo())
                .thenReturn(movieInfoFlux);
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
        Mockito.when(movieInfoService.getMovieInfo(prisonersMovieInfoId))
                        .thenReturn(Mono.just(prisonersMovieInfo));
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
        batmanMovieInfo.setId(prisonersMovieInfoId);
        Mockito.when(movieInfoService.updateMovieInfo(prisonersMovieInfoId, batmanMovieInfo))
                        .thenReturn(Mono.just(batmanMovieInfo));
        webTestClient.put()
                .uri(BASE_URL + "/{id}", prisonersMovieInfoId)
                .bodyValue(batmanMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").isEqualTo(prisonersMovieInfoId)
                .jsonPath("$.name").isEqualTo(batmanMovieInfo.getName());
    }

    @Test
    void deleteMovieInfo() {
        Mockito.when(movieInfoService.deleteMovieInfo(prisonersMovieInfoId))
                .thenReturn(Mono.empty());
        webTestClient.delete()
                .uri(BASE_URL + "/{id}", prisonersMovieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void addMovieInfoWithValidation() {
        MovieInfo invalidMovieInfo = MovieInfo.builder()
                .name("")
                .cast(
                        List.of("")
                )
                .year(-2013)
                .build();
        webTestClient.post()
                .uri(BASE_URL)
                .bodyValue(invalidMovieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(System.out::println);
    }
}