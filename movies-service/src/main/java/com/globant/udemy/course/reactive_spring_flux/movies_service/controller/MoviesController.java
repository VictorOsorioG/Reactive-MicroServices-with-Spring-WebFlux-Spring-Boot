package com.globant.udemy.course.reactive_spring_flux.movies_service.controller;

import com.globant.udemy.course.reactive_spring_flux.movies_service.client.MoviesInfoRestClient;
import com.globant.udemy.course.reactive_spring_flux.movies_service.client.ReviewRestClient;
import com.globant.udemy.course.reactive_spring_flux.movies_service.domain.Movie;
import com.globant.udemy.course.reactive_spring_flux.movies_service.domain.MovieInfo;
import com.globant.udemy.course.reactive_spring_flux.movies_service.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewRestClient reviewRestClient;

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable("id") String movieId) {
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                   Mono<List<Review>> reviewsListMono = reviewRestClient.retrieveReviews(movieId)
                           .collectList();
                   return reviewsListMono.map(reviews -> Movie.builder()
                           .movieInfo(movieInfo)
                           .reviewList(reviews)
                           .build());
                });
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieInfo> getMovieInfos() {
        return moviesInfoRestClient.retrieveMovieInfoStream();
    }
}
