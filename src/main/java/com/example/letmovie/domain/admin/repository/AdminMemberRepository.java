package com.example.letmovie.domain.admin.repository;

import com.example.letmovie.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminMemberRepository extends JpaRepository<Member, Long> {
    // ContainingIgnoreCase : 이름을 포함한 (대소문자구분x)
    List<Member> findByNicknameContainingIgnoreCase(String nickname);
}
