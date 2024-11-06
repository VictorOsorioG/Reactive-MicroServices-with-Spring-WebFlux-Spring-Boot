package com.globant.udemy.course.reactive_spring_flux.movies_info_service.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorTest {

    FluxAndMonoGenerator fluxAndMonoGenerator = new FluxAndMonoGenerator();

    @Test
    void expectNextAllNamesFlux() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFlux();
        StepVerifier.create(namesFlux)
                .expectNext("Joe", "Jane", "Victor")
                .verifyComplete();
    }

    @Test
    void expectCountFlux() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFlux();
        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void expectNameAndExpectCountFlux() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFlux();
        StepVerifier.create(namesFlux)
                .expectNext("Joe")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testNamesFluxMap() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxMap();
        StepVerifier.create(namesFlux)
                .expectNext("JOE", "JANE", "VICTOR")
                .verifyComplete();
    }

    @Test
    void testNamesFluxImmutability() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxImmutability();
        StepVerifier.create(namesFlux)
                .expectNext("Joe", "Jane", "Victor")
                .verifyComplete();
    }

    @Test
    void namesFluxFilterLengthLessThan3() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxFilterLength(3);
        StepVerifier.create(namesFlux)
                .expectNext("4-Jane", "6-Victor")
                .verifyComplete();
    }

    @Test
    void testNamesFluxFlatMap() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxFlatMap();
        StepVerifier.create(namesFlux)
                .expectNext("J", "o", "e", "J", "a", "n", "e")
                .verifyComplete();
    }

    @Test
    void testNamesFluxFlatMapAsync() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxFlatMapAsync();
        StepVerifier.create(namesFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void testNamesFluxConcatMap() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxConcatMap();
        StepVerifier.create(namesFlux)
                .expectNext("J", "o", "e", "J", "a", "n", "e")
                .verifyComplete();
    }

    @Test
    void expectNameMonoFlatMap() {
        Mono<List<String>> nameMono = fluxAndMonoGenerator.nameMonoFlatMap();
        StepVerifier.create(nameMono)
                .expectNext(List.of("V", "i", "c", "t", "o", "r"))
                .verifyComplete();
    }

    @Test
    void expectNameFluxMonoFlatMapMany() {
        Flux<String> nameMono = fluxAndMonoGenerator.nameMonoFlatMapMany();
        StepVerifier.create(nameMono)
                .expectNext("V", "i", "c", "t", "o", "r")
                .verifyComplete();
    }

    @Test
    void testNamesFluxTransform() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxTransform();
        StepVerifier.create(namesFlux)
                .expectNext("JOE", "JANE", "VICTOR")
                .verifyComplete();
    }

    @Test
    void namesFluxFilterLengthLessThan7() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxNoData(7);
        StepVerifier.create(namesFlux)
                .expectNext("Empty")
                .verifyComplete();
    }

    @Test
    void namesFluxFilterLengthLessThan7Switch() {
        Flux<String> namesFlux = fluxAndMonoGenerator.namesFluxSwitchIfEmpty(6);
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void testFluxConcat() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreConcat();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void testFluxConcatWith() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreConcatWith();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void testMonoConcatWith() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreMonoConcatWith();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void testFluxMerge() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreMerge();
        StepVerifier.create(concatFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void testFluxMergeWith() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreMergeWith();
        StepVerifier.create(concatFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void testMonoMergeWith() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreMonoMergeWith();
        StepVerifier.create(concatFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testFluxMergeSequential() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreMergeSequential();
        StepVerifier.create(concatFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void testFluxZip() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreZip();
        StepVerifier.create(concatFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testFluxZip4Flux() {
        Flux<String> concatFlux = fluxAndMonoGenerator.exploreZip4Flux();
        StepVerifier.create(concatFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testMonoZipWith() {
        Mono<String> concatFlux = fluxAndMonoGenerator.exploreMonoZipWith();
        StepVerifier.create(concatFlux)
                .expectNext("AB")
                .verifyComplete();
    }

}