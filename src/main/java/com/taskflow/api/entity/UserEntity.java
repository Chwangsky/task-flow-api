package com.taskflow.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (유니크 제약 조건)

    @Column(nullable = false, unique = true)
    private String nickname; // 닉네임

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(name = "last_password_changed", nullable = false)
    private LocalDateTime lastPasswordChanged; // 마지막 비밀번호 변경 시간

    @Column(name = "tel_number", nullable = false)
    private String telNumber; // 전화번호

    @Column(nullable = false)
    private String address; // 주소

    @Column(name = "address_detail")
    private String addressDetail; // 상세 주소

    @Column(name = "profile_image_url")
    private String profileImageUrl; // 프로필 이미지 URL

    @Column(name = "is_oauth", nullable = false)
    private boolean isOAuth; // OAuth 여부

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified; // 이메일 인증 여부

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate; // 생성일

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate; // 수정일

    @Column(name = "refresh_token")
    private String refreshToken;

}
