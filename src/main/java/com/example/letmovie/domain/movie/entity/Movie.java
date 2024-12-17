package com.example.letmovie.domain.movie.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private int id;
    @Column(nullable = false)
    private String movieName;
    @Column(nullable = false)
    private String movieCode;
    @Column(nullable = false)
    private String directorName;
    @Column(nullable = false)
    private String rating;
    @Column(nullable = false)
    private String runtime;
    @Column(nullable = false)
    private String openDate;
    @Column(nullable = false)
    private String genreName;
    @Column(nullable = false)
    private String companys;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private String posterImageUrl;
    @Column(nullable = false)
    private String stillImageUrl;
    @Column(nullable = false)
    private String plot;
    @Column(nullable = false)
    private String salesAcc;

}
