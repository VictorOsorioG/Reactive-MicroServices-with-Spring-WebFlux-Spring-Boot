package com.globant.udemy.course.reactive_spring_flux.movies_review_service.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    private String id;
    @NotNull(message = "Movie info Id must no be null")
    private Long movieInfoId;
    private String comment;
    @Min(value = 0L, message = "Rating must be a non-negative value")
    private Double rating;
}
