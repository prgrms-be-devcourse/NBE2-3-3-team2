package com.example.nbe232team2.domain.movie.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
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
    private String auditNumber;
    @Column(nullable = false)
    private String showTime;
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
    private String imagename;
}
