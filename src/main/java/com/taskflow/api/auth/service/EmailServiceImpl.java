package com.taskflow.api.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.taskflow.api.auth.dto.request.EmailVerifyRequestDTO;
import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;

    public String verifyToken(EmailVerifyRequestDTO dto) {

        Optional<UserEntity> user;

        log.info("로그!!!{} {}", dto.getUserId(), dto.getToken());

        try {
            user = userRepository.findById(dto.getUserId());
        } catch (DataAccessException e) {
            return "데이터베이스 오류입니다.";
        }

        if (user.isEmpty())
            return "정상적인 접근이 아닙니다.";

        UserEntity userEntity = user.get();

        // 중목 이메일 인증 확인 여부
        if (userEntity.isEmailVerified())
            return "이미 인증이 만료된 메일입니다.";

        // 토큰 만료 여부 확인
        if (userEntity.getEmailVerifiedTokenExpireAt().isBefore(LocalDateTime.now())) {
            return "토큰이 만료되었습니다. 다시 이메일 인증을 진행해주세요.";
        }

        // 토큰 일치 여부 확인
        if (!userEntity.getEmailVerifiedToken().equals(dto.getToken())) {
            return "토큰이 일치하지 않습니다.";
        }

        userEntity.setEmailVerified(true);
        userRepository.save(userEntity);
        return "인증이 완료되었습니다.";

    }

}
