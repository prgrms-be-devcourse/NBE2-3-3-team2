package com.example.letmovie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LetMovieApplication  {

    public static void main(String[] args) {
        SpringApplication.run(LetMovieApplication .class, args);
    }
}
