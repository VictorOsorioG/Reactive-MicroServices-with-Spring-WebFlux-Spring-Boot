package com.globant.udemy.course.reactive_spring_flux.movies_review_service.repository;

import com.globant.udemy.course.reactive_spring_flux.movies_review_service.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findReviewsByMovieInfoId(Long movieInfoId);
}
