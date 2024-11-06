package com.globant.udemy.course.reactive_spring_flux.movies_service.client;

import com.globant.udemy.course.reactive_spring_flux.movies_service.domain.MovieInfo;
import com.globant.udemy.course.reactive_spring_flux.movies_service.exception.MoviesInfoClientException;
import com.globant.udemy.course.reactive_spring_flux.movies_service.exception.MoviesInfoServerException;
import com.globant.udemy.course.reactive_spring_flux.movies_service.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MoviesInfoRestClient {

    private final WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        String url = moviesInfoUrl.concat("/{id}");

        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException("No movie info available"));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(responseMessage)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException("Server is down " + responseMessage))))
                .bodyToMono(MovieInfo.class)
                //.retry(3)
                .retryWhen(RetryUtil.retrySpec(3,1))
                .log();
    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        return webClient.get()
                .uri(moviesInfoUrl + "/stream")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException("No movie info available"));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(responseMessage)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException("Server is down " + responseMessage))))
                .bodyToFlux(MovieInfo.class)
                .retryWhen(RetryUtil.retrySpec(3,1))
                .log();
    }
}
