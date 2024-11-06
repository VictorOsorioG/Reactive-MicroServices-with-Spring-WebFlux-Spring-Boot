package com.globant.udemy.course.reactive_spring_flux.movies_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private MovieInfo movieInfo;
    private List<Review> reviewList;
}
