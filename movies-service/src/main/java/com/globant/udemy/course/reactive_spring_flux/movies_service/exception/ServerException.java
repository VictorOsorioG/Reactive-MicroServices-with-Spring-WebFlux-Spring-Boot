package com.globant.udemy.course.reactive_spring_flux.movies_service.exception;

public class ServerException extends RuntimeException{
    public ServerException(String message) {
        super(message);
    }
}
