package com.globant.udemy.course.reactive_spring_flux.movies_service.exception;

public class MoviesInfoClientException extends RuntimeException{
    private String message;

    public MoviesInfoClientException(String message) {
        super(message);
        this.message = message;
    }
}
