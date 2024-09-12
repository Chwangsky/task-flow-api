package com.taskflow.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.api.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 이메일로 사용자를 조회하는 메소드
    Optional<UserEntity> findByEmail(String email);

    // 이메일 중복여부 확인
    Boolean existsByEmailAndIsOAuth(String email, Boolean isOAuth);

    // 닉네임 중복 여부 확인
    Boolean existsByNickname(String nickname);

    // 닉네임으로 사용자를 조회하는 메소드
    Optional<UserEntity> findByNickname(String nickname);

    // OAuth 로그인 여부로 사용자를 조회하는 메소드
    Optional<UserEntity> findByIsOAuth(boolean isOAuth);

    // 이메일이 인증된 사용자를 조회하는 메소드
    Optional<UserEntity> findByEmailAndIsEmailVerified(String email, boolean isEmailVerified);

    Optional<UserEntity> findByEmailAndIsOAuth(String email, boolean isOAuth);

}