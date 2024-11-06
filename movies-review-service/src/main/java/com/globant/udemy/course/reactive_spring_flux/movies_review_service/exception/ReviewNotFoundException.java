package com.globant.udemy.course.reactive_spring_flux.movies_review_service.exception;

public class ReviewNotFoundException extends RuntimeException{
    private String message;

    public ReviewNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
