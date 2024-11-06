package com.globant.udemy.course.reactive_spring_flux.movies_info_service.repository;

import com.globant.udemy.course.reactive_spring_flux.movies_info_service.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

    Flux<MovieInfo> findByYear(Integer year);
    Mono<MovieInfo> findByName(String name);
}
