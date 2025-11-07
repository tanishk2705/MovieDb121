package com.CineTrust.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String genre;

    @Column(length = 50)
    private String language;

    @Column(length = 100)
    private String director;

    @Column(columnDefinition = "TEXT")
    private String cast; // Comma-separated actor names

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @Column(nullable = false)
    private Float rating = 0.0f;

    @Column(nullable = false)
    private Boolean approved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}

