package com.globant.udemy.course.reactive_spring_flux.movies_review_service.exception;

public class ReviewDataException extends RuntimeException{
    private String message;

    public ReviewDataException(String message) {
        super(message);
        this.message = message;
    }
}
