package com.globant.udemy.course.reactive_spring_flux.movies_review_service.router;

import com.globant.udemy.course.reactive_spring_flux.movies_review_service.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ReviewRouter {

    private final String BASE_URL = "api/v1/movies-review";

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path(BASE_URL + "/reviews"), builder -> {
                    builder.POST(reviewHandler::addReview)
                            .GET("/stream", reviewHandler::getReviewsStream)
                            .GET(reviewHandler::getReviews)
                            .PUT("/{id}", reviewHandler::updateReview)
                            .DELETE("/{id}", reviewHandler::deleteReview);
                })
                .GET(BASE_URL + "/hello", (request -> ServerResponse.ok().bodyValue("Hello")))
                .build();
    }
}
