package com.globant.udemy.course.reactive_spring_flux.movies_info_service.service;

import com.globant.udemy.course.reactive_spring_flux.movies_info_service.domain.MovieInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoService {
    Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo);

    Flux<MovieInfo> getMoviesInfo();

    Mono<Void> deleteMovieInfo(String id);

    Mono<MovieInfo> getMovieInfo(String id);

    Mono<MovieInfo> updateMovieInfo(String id, MovieInfo updatedMovieInfo);

    Flux<MovieInfo> getMoviesInfoByYear(Integer year);

}
