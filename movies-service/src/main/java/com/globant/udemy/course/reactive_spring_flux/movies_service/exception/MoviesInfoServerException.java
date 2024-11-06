package com.globant.udemy.course.reactive_spring_flux.movies_service.exception;

public class MoviesInfoServerException extends ServerException{
    private String message;

    public MoviesInfoServerException(String message) {
        super(message);
        this.message = message;
    }
}
