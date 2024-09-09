package com.taskflow.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자를 조회하는 메소드
    Optional<User> findByEmail(String email);

    // 닉네임으로 사용자를 조회하는 메소드
    Optional<User> findByNickname(String nickname);

    // OAuth 로그인 여부로 사용자를 조회하는 메소드
    Optional<User> findByIsOauth(boolean isOauth);

    // 이메일이 인증된 사용자를 조회하는 메소드
    Optional<User> findByEmailAndIsEmailVerified(String email, boolean isEmailVerified);

    Optional<User> findByEmailAndIsOAuth(String email, boolean isOAuth);
}