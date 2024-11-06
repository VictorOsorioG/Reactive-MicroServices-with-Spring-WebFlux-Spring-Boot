package com.globant.udemy.course.reactive_spring_flux.movies_service.util;

import com.globant.udemy.course.reactive_spring_flux.movies_service.exception.ServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

public class RetryUtil {
    public static Retry retrySpec(long maxAttempts, long secondDuration) {
        return Retry.fixedDelay(maxAttempts, Duration.ofSeconds(secondDuration))
                .filter(ex -> ex instanceof ServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));
    }
}
