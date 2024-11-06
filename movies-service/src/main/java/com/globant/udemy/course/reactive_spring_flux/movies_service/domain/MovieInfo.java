package com.globant.udemy.course.reactive_spring_flux.movies_service.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieInfo {
    private String id;
    @NotBlank(message = "Name must be present")
    private String name;
    @NotNull
    @Positive(message = "Year must be a positive value")
    private Integer year;
    private List<@NotBlank(message = "Cast must be present") String> cast;
    private LocalDate releaseDate;
}