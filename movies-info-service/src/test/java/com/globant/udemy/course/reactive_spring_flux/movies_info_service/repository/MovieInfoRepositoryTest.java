package com.globant.udemy.course.reactive_spring_flux.movies_info_service.repository;

import com.globant.udemy.course.reactive_spring_flux.movies_info_service.domain.MovieInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    private MovieInfo prisonersMovieInfo;

    @BeforeEach
    void setUp() {
        prisonersMovieInfo =   MovieInfo.builder()
                .id("qwerty123456")
                .name("Prisoners")
                .cast(
                        List.of("Hugh Jackman", "Jake Gyllenhaal")
                )
                .year(2013)
                .releaseDate(LocalDate.parse("2013-09-20"))
                .build();
        movieInfoRepository.save(prisonersMovieInfo)
                .block();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll()
                .block();
    }

    @Test
    void findAll() {
        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfoFlux)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findById("qwerty123456").log();
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> assertEquals(prisonersMovieInfo, movieInfo))
                .verifyComplete();
    }

    @Test
    void saveAndDelete() {
        MovieInfo oppenheimerMovieInfo = MovieInfo.builder()
                .id("1234567")
                .name("Oppenheimer")
                .cast(
                        List.of("Cillian Murphy", "Emily Blunt")
                )
                .year(2023)
                .releaseDate(LocalDate.parse("2023-07-21"))
                .build();
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.save(oppenheimerMovieInfo).log();
        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> assertEquals(oppenheimerMovieInfo, movieInfo))
                .verifyComplete();
        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
        movieInfoRepository.delete(oppenheimerMovieInfo).block();
        Flux<MovieInfo> movieInfoFlux2 = movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfoFlux2)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByYear() {
        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findByYear(2013).log();
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    void findByName() {
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findByName("Prisoners").log();
        StepVerifier.create(movieInfoMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}