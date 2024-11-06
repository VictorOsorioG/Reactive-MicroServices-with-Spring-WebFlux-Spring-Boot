package com.globant.udemy.course.reactive_spring_flux.movies_info_service.exception;

public class MovieInfoNotFoundException extends RuntimeException{
    public MovieInfoNotFoundException() {
        super("Movie Not found with the provided data");
    }
}
