package com.example.letmovie.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @Column(name ="seat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



}
