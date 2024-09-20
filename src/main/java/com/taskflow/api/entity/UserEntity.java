package com.taskflow.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.taskflow.api.auth.dto.request.LocalSignUpRequestDTO;

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
@DynamicUpdate
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname; // 닉네임

    @Column(nullable = false, unique = false)
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

    @Column(name = "email_verified_token", nullable = true)
    private String emailVerifiedToken;

    @Column(name = "email_verified_token_expire_at")
    private LocalDateTime emailVerifiedTokenExpireAt;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate; // 생성일

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate; // 수정일

    @Column(name = "refresh_token")
    private String refreshToken;

    // 엔티티 영속화 전 생성일, 수정일 자동 설정
    @PrePersist
    private void onCreate() {
        // this.lastPasswordChanged = LocalDateTime.now(); // 이건 서비스로직에서 적용함이 타당해보임.
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    public static UserEntity emailVerifiyEntityFromDTO(LocalSignUpRequestDTO dto, String encodedPassword, String token,
            LocalDateTime expireAt) {
        return UserEntity.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .telNumber(dto.getTelNumber())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .isOAuth(false)
                .isEmailVerified(false)
                .lastPasswordChanged(LocalDateTime.now())
                .emailVerifiedToken(token)
                .emailVerifiedTokenExpireAt(expireAt)
                .build();
    }

}
