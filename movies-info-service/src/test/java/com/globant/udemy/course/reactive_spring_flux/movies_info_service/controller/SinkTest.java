package com.globant.udemy.course.reactive_spring_flux.movies_info_service.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {

    @Test
    void sink() {
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        Flux<Integer> integerFlux = replaySink.asFlux();
        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux.subscribe(integer -> System.out.println("Subscriber 1"));
        integerFlux2.subscribe(integer -> System.out.println("Subscriber 2"));
        replaySink.tryEmitNext(3);
    }

    @Test
    void sinkMulticast() {
        Sinks.Many<Integer> replaySink = Sinks.many().multicast().onBackpressureBuffer();
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        Flux<Integer> integerFlux = replaySink.asFlux();
        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux.subscribe(integer -> System.out.println("Subscriber 1"));
        integerFlux2.subscribe(integer -> System.out.println("Subscriber 2"));
        replaySink.tryEmitNext(3);
    }

    @Test
    void sinkUnicast() {
        Sinks.Many<Integer> replaySink = Sinks.many().unicast().onBackpressureBuffer();
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        Flux<Integer> integerFlux = replaySink.asFlux();
        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux.subscribe(System.out::println);
        integerFlux2.subscribe(System.out::println);
        replaySink.tryEmitNext(3);
    }
}
