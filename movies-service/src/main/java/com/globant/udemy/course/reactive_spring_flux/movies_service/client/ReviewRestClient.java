package com.globant.udemy.course.reactive_spring_flux.movies_service.client;

import com.globant.udemy.course.reactive_spring_flux.movies_service.domain.Review;
import com.globant.udemy.course.reactive_spring_flux.movies_service.exception.ReviewClientException;
import com.globant.udemy.course.reactive_spring_flux.movies_service.exception.ReviewServerException;
import com.globant.udemy.course.reactive_spring_flux.movies_service.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReviewRestClient {

    private final WebClient webClient;

    @Value("${restClient.moviesReviewUrl}")
    private String moviesReviewUrl;

    public Flux<Review> retrieveReviews(String movieId) {
        String url = UriComponentsBuilder.fromHttpUrl(moviesReviewUrl)
                .queryParam("movieInfoId", movieId)
                .toUriString();
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new ReviewClientException(responseMessage)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseMessage -> Mono.error(new ReviewServerException("Server is down"))))
                .bodyToFlux(Review.class)
                .retryWhen(RetryUtil.retrySpec(2,1))
                .log();
    }
}
