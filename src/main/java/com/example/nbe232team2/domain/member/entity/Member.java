package com.example.nbe232team2.domain.member.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false, unique = true, length = 20)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(nullable = false, length = 5)
    private String zipCode;

    @Column(nullable = false)
    private boolean isAdmin = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.GENERAL;
}
