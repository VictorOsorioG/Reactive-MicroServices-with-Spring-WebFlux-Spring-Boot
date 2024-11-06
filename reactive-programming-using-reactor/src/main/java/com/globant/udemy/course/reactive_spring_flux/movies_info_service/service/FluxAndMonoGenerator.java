package com.globant.udemy.course.reactive_spring_flux.movies_info_service.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGenerator {

    private static FluxAndMonoGenerator fluxAndMonoGenerator;

    public static void main(String[] args) {
        fluxAndMonoGenerator = new FluxAndMonoGenerator();
        System.out.println("FLUX");
        subscribeToFlux();
        System.out.println("\nMONO");
        subscribeToMono();
    }

    public static void subscribeToFlux() {
        fluxAndMonoGenerator.namesFlux()
                .subscribe(System.out::println);
    }

    public static void subscribeToMono() {
        fluxAndMonoGenerator.nameMono()
                .subscribe(System.out::println);
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Joe", "Jane", "Victor"))
                .log();
    }

    public Mono<String> nameMono() {
        return Mono.just("Victor")
                .log();
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("Joe", "Jane", "Victor"))
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFluxImmutability() {
        Flux<String> names = Flux.fromIterable(List.of("Joe", "Jane", "Victor")).log();
        names.map(String::toUpperCase);
        return names;
    }

    public Flux<String> namesFluxFilterLength(int nameLength) {
        return Flux.fromIterable(List.of("Joe", "Jane", "Victor"))
                .filter(name -> name.length() > nameLength)
                .map(name -> name.length() + "-" + name)
                .log();
    }

    public Flux<String> namesFluxFlatMap() {
        return Flux.fromIterable(List.of("Joe", "Jane"))
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFluxFlatMapAsync() {
        return Flux.fromIterable(List.of("Joe", "Jane"))
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> namesFluxConcatMap() {
        return Flux.fromIterable(List.of("Joe", "Jane"))
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    public Mono<List<String>> nameMonoFlatMap() {
        return Mono.just("Victor")
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> nameMonoFlatMapMany() {
        return Mono.just("Victor")
                .flatMapMany(this::splitString)
                .log();
    }

    public Flux<String> namesFluxTransform() {
        Function<Flux<String>, Flux<String>> filterer = name -> name.map(String::toUpperCase);
        return Flux.fromIterable(List.of("Joe", "Jane", "Victor"))
                .transform(filterer)
                .log();
    }

    public Flux<String> namesFluxNoData(int nameLength) {
        return Flux.fromIterable(List.of("Joe", "Jane", "Victor"))
                .filter(name -> name.length() > nameLength)
                .defaultIfEmpty("Empty")
                .log();
    }

    public Flux<String> namesFluxSwitchIfEmpty(int nameLength) {
        Function<Flux<String>, Flux<String>> filterer = name -> name.filter(s -> s.length() > nameLength);
        Flux<String> defaultFlux = Flux.just("default")
                .transform(filterer);
        return Flux.fromIterable(List.of("Joe", "Jane", "Victor"))
                .transform(filterer)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> exploreConcat() {
        Flux<String> abcFlux = Flux.just("A", "B", "C").log();
        Flux<String> defFlux = Flux.just("D", "E", "F").log();

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> exploreConcatWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> exploreMonoConcatWith() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");
        return aMono.concatWith(bMono).log();
    }

    public Flux<String> exploreMerge() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100))
                .log();
        Flux<String> defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125))
                .log();
        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> exploreMergeWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100))
                .log();
        Flux<String> defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125))
                .log();
        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> exploreMonoMergeWith() {
        Mono<String> aMono = Mono.just("A")
                .delayElement(Duration.ofMillis(100))
                .log();
        Mono<String> bMono = Mono.just("B")
                .delayElement(Duration.ofMillis(125))
                .log();
        return aMono.mergeWith(bMono).log();
    }

    public Flux<String> exploreMergeSequential() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100))
                .log();
        Flux<String> defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125))
                .log();
        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> exploreZip() {
        Flux<String> abcFlux = Flux.just("A", "B", "C").log();
        Flux<String> defFlux = Flux.just("D", "E", "F").log();

        return Flux.zip(abcFlux, defFlux, (firsts, second) -> firsts + second).log();
    }

    public Flux<String> exploreZip4Flux() {
        Flux<String> abcFlux = Flux.just("A", "B", "C").log();
        Flux<String> defFlux = Flux.just("D", "E", "F").log();
        Flux<String> _123Flux = Flux.just("1", "2", "3").log();
        Flux<String> _456Flux = Flux.just("6", "5", "6").log();

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }

    public Mono<String> exploreMonoZipWith() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");
        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }


    private Flux<String> splitString(String name) {
        return Flux.fromArray(name.split(""));
    }

    private Flux<String> splitStringWithDelay(String name) {
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(name.split(""))
                .delayElements(Duration.ofMillis(delay));
    }

    private Mono<List<String>> splitStringMono(String name) {
        return Mono.just(List.of(name.split("")));
    }

}
