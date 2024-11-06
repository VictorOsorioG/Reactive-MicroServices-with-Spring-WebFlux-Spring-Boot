package com.globant.udemy.course.reactive_spring_flux.movies_review_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Exception message is: {}", ex.getMessage());
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer errorMessage = dataBufferFactory.wrap(ex.getMessage().getBytes());
        if (ex instanceof ReviewDataException) {
            return writeExchangeResponse(exchange, HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (ex instanceof ReviewNotFoundException) {
            return writeExchangeResponse(exchange, HttpStatus.NOT_FOUND, errorMessage);
        }
        return writeExchangeResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    private static Mono<Void> writeExchangeResponse(ServerWebExchange exchange, HttpStatus badRequest, DataBuffer errorMessage) {
        exchange.getResponse().setStatusCode(badRequest);
        return exchange.getResponse().writeWith(Mono.just(errorMessage));
    }
}
