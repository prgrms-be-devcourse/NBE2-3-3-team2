package com.example.letmovie.domain.admin.repository;

import com.example.letmovie.domain.reservation.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminScreenRepository extends JpaRepository<Screen, Long> {
}
