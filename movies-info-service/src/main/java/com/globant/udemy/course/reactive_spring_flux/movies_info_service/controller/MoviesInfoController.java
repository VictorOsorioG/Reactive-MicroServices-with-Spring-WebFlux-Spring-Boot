package com.globant.udemy.course.reactive_spring_flux.movies_info_service.controller;

import com.globant.udemy.course.reactive_spring_flux.movies_info_service.exception.MovieInfoNotFoundException;
import com.globant.udemy.course.reactive_spring_flux.movies_info_service.service.MovieInfoService;
import com.globant.udemy.course.reactive_spring_flux.movies_info_service.domain.MovieInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies-info")
public class MoviesInfoController {

    private final MovieInfoService movieInfoService;
    Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().latest();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo)
                .doOnNext(savedMovieInfo -> movieInfoSink.tryEmitNext(savedMovieInfo));
    }

    @GetMapping
    public Flux<MovieInfo> getMoviesInfo(@RequestParam(value = "year", required = false) Integer year) {
        return Objects.nonNull(year) ? movieInfoService.getMoviesInfoByYear(year) : movieInfoService.getMoviesInfo();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieInfo> getMoviesInfo() {
        return movieInfoSink.asFlux()
                .log();
    }

    @GetMapping("/{id}")
    public Mono<MovieInfo> getMovieInfo(@PathVariable String id) {
        return movieInfoService.getMovieInfo(id)
                .switchIfEmpty(Mono.error(new MovieInfoNotFoundException()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id, @RequestBody MovieInfo updatedMovieInfo) {
        return movieInfoService.updateMovieInfo(id, updatedMovieInfo)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return movieInfoService.deleteMovieInfo(id);
    }
}
