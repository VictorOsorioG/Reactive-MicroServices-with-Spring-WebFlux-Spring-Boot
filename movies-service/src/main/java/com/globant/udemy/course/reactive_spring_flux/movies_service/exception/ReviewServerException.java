package com.globant.udemy.course.reactive_spring_flux.movies_service.exception;

public class ReviewServerException extends ServerException{
    private String message;

    public ReviewServerException(String message) {
        super(message);
        this.message = message;
    }
}
