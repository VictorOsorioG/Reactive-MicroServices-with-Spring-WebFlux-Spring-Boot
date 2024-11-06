package com.globant.udemy.course.reactive_spring_flux.movies_service.exception;

public class ReviewClientException extends RuntimeException{
    private String message;

    public ReviewClientException(String message) {
        super(message);
        this.message = message;
    }
}
