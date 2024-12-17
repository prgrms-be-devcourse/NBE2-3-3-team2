package com.example.letmovie.domain.reservation.entity;

import jakarta.persistence.*;

@Entity
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;


}
