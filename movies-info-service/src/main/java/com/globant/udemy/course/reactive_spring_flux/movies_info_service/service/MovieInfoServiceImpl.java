package com.globant.udemy.course.reactive_spring_flux.movies_info_service.service;

import com.globant.udemy.course.reactive_spring_flux.movies_info_service.domain.MovieInfo;
import com.globant.udemy.course.reactive_spring_flux.movies_info_service.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieInfoServiceImpl implements MovieInfoService {

    private final MovieInfoRepository movieInfoRepository;

    @Override
    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo).log();
    }

    @Override
    public Flux<MovieInfo> getMoviesInfo() {
        return movieInfoRepository.findAll().log();
    }

    @Override
    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id).log();
    }

    @Override
    public Mono<MovieInfo> getMovieInfo(String id) {
        return movieInfoRepository.findById(id).log();
    }

    @Override
    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo updatedMovieInfo) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfo -> {
                    updateMovieInfoEntity(movieInfo, updatedMovieInfo);
                    return movieInfoRepository.save(movieInfo);
                })
                .log();
    }

    @Override
    public Flux<MovieInfo> getMoviesInfoByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }

    private void updateMovieInfoEntity(MovieInfo movieInfo, MovieInfo updatedMovieInfo) {
        movieInfo.setName(updatedMovieInfo.getName());
        movieInfo.setCast(updatedMovieInfo.getCast());
        movieInfo.setYear(updatedMovieInfo.getYear());
        movieInfo.setReleaseDate(updatedMovieInfo.getReleaseDate());
    }


}
